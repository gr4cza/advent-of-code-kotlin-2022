package day24

import Direction
import Position
import readInput

fun main() {

    fun simulate(valley: Valley): Int {
        var i = 0
        var possiblePositions = setOf(valley.startingPos)
        while (!possiblePositions.contains(valley.endingPos)) {
            valley.step()
            possiblePositions = possiblePositions.map { pos ->
                valley.possiblePositions(pos)
            }.flatten().toSet()
            i++
        }
        return i
    }

    fun part1(input: List<String>): Int {
        val valley = Valley(input)
        return simulate(valley)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day24/Day24_test")
    val input = readInput("day24/Day24")

    check((part1(testInput)).also { println(it) } == 18)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 1)
    println(part2(input))
}

class Valley(
    input: List<String>,
) {
    private val width: Int
    private val height: Int
    val startingPos: Position
    val endingPos: Position
    private val blizzards: List<Blizzard>

    init {
        width = input.first().length
        height = input.size
        startingPos = Position(input.first().indexOfFirst { it == '.' }, 0)
        endingPos = Position(input.last().indexOfFirst { it == '.' }, height - 1)
        blizzards = input.mapIndexed { y, row ->
            row.mapIndexedNotNull { x, cell ->
                if (cell !in "#.") {
                    Blizzard(pos = Position(x, y), Direction.fromDir(cell))
                } else {
                    null
                }
            }
        }.flatten()
    }

    override fun toString(): String {
        return (0 until height).joinToString("\n") { y ->
            (0 until width).joinToString("") { x ->
                val pos = Position(x, y)
                val blizzardPositions = blizzards.map { it.pos }
                when {
                    isWall(pos) -> "#"
                    pos in blizzardPositions -> drawBlizzard(blizzards.filter { it.pos == pos })
                    else -> "."
                }
            }
        }
    }

    private fun drawBlizzard(filter: List<Blizzard>): String {
        return if (filter.size == 1) {
            toString(filter.first().dir)
        } else {
            filter.size.toString()
        }
    }

    private fun isWall(pos: Position): Boolean {
        return ((pos.x in listOf(0, width - 1)) || (pos.y in listOf(0, height - 1)))
            && pos !in listOf(startingPos, endingPos)
    }

    fun step() {
        blizzards.forEach { blizz ->
            val newPosition = blizz.pos.newPosition(blizz.dir)
            if (isWall(newPosition)) {
                when (blizz.dir) {
                    Direction.U -> blizz.pos = blizz.pos.copy(y = height - 2)
                    Direction.D -> blizz.pos = blizz.pos.copy(y = 1)
                    Direction.L -> blizz.pos = blizz.pos.copy(x = width - 2)
                    Direction.R -> blizz.pos = blizz.pos.copy(x = 1)
                }
            } else {
                blizz.pos = newPosition
            }
        }
    }

    fun possiblePositions(pos: Position): List<Position> {
        val blizzardPositions = blizzards.map { it.pos }.toSet()
        return listOf(
            pos,
            pos.newPosition(Direction.U),
            pos.newPosition(Direction.R),
            pos.newPosition(Direction.D),
            pos.newPosition(Direction.L),
        ).filter { it !in blizzardPositions && !isWall(it) }
    }
}

private fun toString(dir: Direction): String {
    return when (dir) {
        Direction.U -> "^"
        Direction.D -> "v"
        Direction.L -> "<"
        Direction.R -> ">"
    }
}

private fun Direction.Companion.fromDir(cell: Char): Direction = when (cell) {
    '^' -> Direction.U
    '>' -> Direction.R
    'v' -> Direction.D
    '<' -> Direction.L
    else -> error("Wrong input")
}

data class Blizzard(
    var pos: Position,
    val dir: Direction,
)
