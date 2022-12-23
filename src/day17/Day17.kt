@file:Suppress("MagicNumber")

package day17

import Direction
import Position
import readInput

private const val CHAMBER_WIDTH = 7

fun main() {
    fun parse(input: List<String>): List<Direction> = input.first().chunked(1).map {
        when (it) {
            ">" -> Direction.R
            "<" -> Direction.L
            else -> error("Wrong input")
        }
    }

    fun part1(input: List<String>, iterations: Int): Long {
        val directions = parse(input)
        val chamber = Chamber()
        val rocks = RockShape.values().toList()
        chamber.simulateFalling(iterations.toLong(), directions, rocks)
        return chamber.getAbsoluteRockHeight()
    }

    fun part2(input: List<String>, iterations: Long): Long {
        val directions = parse(input)
        val chamber = Chamber()
        val rocks = RockShape.values().toList()
        chamber.simulateFalling(iterations, directions, rocks)
        return chamber.getAbsoluteRockHeight()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day17/Day17_test")
    val input = readInput("day17/Day17")

    check((part1(testInput, 2022)).also { println(it) } == 3068L)
    check((part1(input, 2022)).also { println(it) } == 3157L)
    check(part2(testInput, 1_000_000_000_000L).also { println(it) } == 1514285714288L)
    println(part2(input, 1_000_000_000_000L))
}

class Chamber {
    private val windowHeight = 500
    private val padding = windowHeight / 3
    private var absoluteHeight = 0L
    private var calculatedHeight = 0L
    private val lastAddedPositions = ArrayDeque<Position>()
    private var grid: List<MutableList<Boolean>> =
        List(windowHeight) { MutableList(CHAMBER_WIDTH) { false } }


    fun getAbsoluteRockHeight(): Long {
        grid.forEachIndexed { index, rocks ->
            if (rocks.any { it }) {
                return absoluteHeight + windowHeight - index + calculatedHeight
            }
        }
        return 0L
    }

    private fun rockHeight(): Int {
        grid.forEachIndexed { index, rocks ->
            if (rocks.any { it }) {
                return windowHeight - index
            }
        }
        return 0
    }

    fun simulateFalling(iteration: Long, directions: List<Direction>, rockShapes: List<RockShape>) {
        var dirCount = 0
        var rockCount = 0

        val states = mutableMapOf<State, Pair<Long, Long>>()
        var foundRepetition = false

        var i = 0L
        while (i < iteration) {
            val currentRock = rockCount++ % rockShapes.size

            val state = State(currentRock, dirCount % directions.size, grid.hashCode())
            if (!foundRepetition && states.contains(state)) {
                val (index, height) = states.getValue(state)
                val reputationLength = i - index
                val remainingReps = (iteration - i) / reputationLength
                calculatedHeight = remainingReps * (getAbsoluteRockHeight() - height)
                i += reputationLength * remainingReps
                foundRepetition = true
            } else {
                states[state] = i to getAbsoluteRockHeight()
            }

            val rockShape = rockShapes[currentRock]
            val rock = Rock(Position(2, windowHeight - this.rockHeight() - rockShape.height() - 3), rockShape)

            var moving = true
            while (moving) {
                val direction = directions[dirCount++ % directions.size]
                tryToMove(rock, direction)
                moving = tryToMove(rock, Direction.D)
                if (!moving) {
                    addRock(rock)
                    savePosition(rock)
                }
            }
            tryToMoveWindow()
            i++
        }
    }

    private fun tryToMoveWindow() {
        if (lastAddedPositions.all { it.y <= windowHeight - padding }) {
            grid = List(padding) { MutableList(CHAMBER_WIDTH) { false } } + grid.take(windowHeight - padding)
            absoluteHeight += padding
            lastAddedPositions.forEach { it.y += padding }
        }
    }

    private fun savePosition(rock: Rock) {
        lastAddedPositions.addFirst(rock.currentPos)
        while (lastAddedPositions.size > padding) {
            lastAddedPositions.removeLast()
        }
    }

    private fun tryToMove(rock: Rock, direction: Direction): Boolean {
        val currentPos = rock.currentPos
        rock.move(direction)
        return if (checkMove(rock)) {
            true
        } else {
            rock.currentPos = currentPos
            false
        }
    }

    private fun checkMove(rock: Rock): Boolean {
        val pos = rock.currentPos
        rock.shape.shape.forEachIndexed { y, row ->
            row.forEachIndexed { x, element ->
                val checkedY = pos.y + y
                val checkedX = pos.x + x

                if (element) {
                    if (checkedY !in 0 until windowHeight) return false
                    if (checkedX !in 0 until CHAMBER_WIDTH) return false
                    if (grid[checkedY][checkedX]) return false
                }
            }
        }
        return true
    }

    private fun addRock(rock: Rock) {
        val pos = rock.currentPos
        rock.shape.shape.forEachIndexed { y, row ->
            row.forEachIndexed { x, element ->
                if (element) {
                    val checkedY = pos.y + y
                    val checkedX = pos.x + x
                    grid[checkedY][checkedX] = true
                }
            }
        }
    }

    override fun toString(): String {
        return grid.joinToString("\n") { line ->
            line.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    false -> "."
                    true -> "@"
                }
            }
        }
    }
}

data class Rock(
    var currentPos: Position,
    val shape: RockShape,
) {
    fun move(direction: Direction) {
        if (direction == Direction.U) {
            error("Invalid direction")
        }
        currentPos = currentPos.newPosition(direction)
    }
}

enum class RockShape(val shape: List<List<Boolean>>) {
    A(
        listOf(
            listOf(true, true, true, true)
        )
    ),
    B(
        listOf(
            listOf(false, true, false),
            listOf(true, true, true),
            listOf(false, true, false),
        )
    ),
    C(
        listOf(
            listOf(false, false, true),
            listOf(false, false, true),
            listOf(true, true, true),
        )
    ),
    D(
        listOf(
            listOf(true),
            listOf(true),
            listOf(true),
            listOf(true),
        )
    ),
    E(
        listOf(
            listOf(true, true),
            listOf(true, true),
        )
    ),
    ;

    fun height(): Int {
        return shape.size
    }

    override fun toString(): String {
        return shape.joinToString("\n") { line ->
            line.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    false -> "."
                    true -> "#"
                }
            }
        }
    }
}

data class State(
    val dirCount: Int,
    val rockCount: Int,
    val topOfRocks: Int,
)
