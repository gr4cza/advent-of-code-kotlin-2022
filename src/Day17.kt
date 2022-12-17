@file:Suppress("MagicNumber")
import kotlin.experimental.and
import kotlin.experimental.or


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
    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

//    check((part1(testInput, 2022)).also { println(it) } == 3068L)
//    check((part1(input, 2022)).also { println(it) } == 3157L)
    check(part2(testInput, 1_000_000_000_000L).also { println(it) } == 1514285714288)
    println(part2(input, 1_000_000_000_000L))
}

class Chamber {
    private val windowHeight = 200
    private val padding = 50
    private var absoluteHeight = 0L
    private val lastAddedPositions = ArrayDeque<Position>()
    private var grid: MutableList<Byte> =
        MutableList(windowHeight) { 0b0000000 }


    fun getAbsoluteRockHeight(): Long {
        grid.forEachIndexed { index, rocks ->
            if (rocks > 0b0000000) {
                return absoluteHeight + windowHeight - index
            }
        }
        return 0L
    }

    private fun rockHeight(): Int {
        grid.forEachIndexed { index, rocks ->
            if (rocks > 0b0000000) {
                return windowHeight - index
            }
        }
        return 0
    }

    fun simulateFalling(iteration: Long, directions: List<Direction>, rockShapes: List<RockShape>) {
        var dirCount = 0
        var rockCount = 0

        (0 until iteration).forEach { i ->
            val rockShape = rockShapes[rockCount++ % rockShapes.size]
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
            if (i % 1_000_000 == 0L) println(i)
        }
    }

    private fun tryToMoveWindow() {
        if (lastAddedPositions.all { it.y <= windowHeight - padding }) {
            grid = (MutableList(padding) { 0.toByte() } + grid.take(windowHeight - padding)).toMutableList()
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
        rock.shape.forEachIndexed { y, row ->
            val checkedY = pos.y + y
            if (checkedY !in 0 until windowHeight) return false
            if (row and grid[checkedY] > 0) return false

        }
        return true
    }

    private fun addRock(rock: Rock) {
        val pos = rock.currentPos
        rock.shape.forEachIndexed { y, row ->
            val checkedY = pos.y + y
            grid[checkedY] = row or grid[checkedY]
        }
    }

    override fun toString(): String {
        return grid.joinToString("\n") { line ->
            line.toInt().toBitString(7).chunked(1).map { it.toInt() }.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    0 -> "."
                    1 -> "@"
                    else -> error("wrong input")
                }
            }
        }
    }
}

class Rock(
    var currentPos: Position,
    val rockShape: RockShape,
) {
    val shape: MutableList<Byte> = rockShape.shape.toMutableList()
    fun move(direction: Direction) {
        when (direction) {
            Direction.U -> {
                error("Invalid direction")
            }
            Direction.D -> currentPos.newPosition(direction)
            Direction.L -> {

            }
            Direction.R -> {

            }
        }
        currentPos = currentPos.newPosition(direction)
    }
}

enum class RockShape(
    val shape: List<Byte>,
    val possibleRange: IntRange
) {
    A(
        listOf(0b0011110),
        0..3
    ),
    B(
        listOf(
            0b0001000,
            0b0011100,
            0b0001000,
        ),
        0..4
    ),
    C(
        listOf(
            0b0000100,
            0b0000100,
            0b0011100,
        ),
        0..4
    ),
    D(
        listOf(
            0b0010000,
            0b0010000,
            0b0010000,
            0b0010000,
        ),
        0..6
    ),
    E(
        listOf(
            0b0011000,
            0b0011000,
        ),
        0..6
    ),
    ;

    fun height(): Int {
        return shape.size
    }

    override fun toString(): String {
        return shape.joinToString("\n") { line ->
            line.toInt().toBitString(7).chunked(1).map { it.toInt() }.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    0 -> "."
                    1 -> "@"
                    else -> error("wrong input")
                }
            }
        }
    }
}
