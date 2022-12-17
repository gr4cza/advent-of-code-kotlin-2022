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
    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

    check((part1(testInput, 2022)).also { println(it) } == 3068L)
    check((part1(input, 2022)).also { println(it) } == 3157L)
    check(part2(testInput, 1_000_000_000_000L).also { println(it) } == 1514285714288)
    println(part2(input, 1_000_000_000_000L))
}

class Chamber() {
    private val windowHeight = 5000
    private var absoluteHeight = 0L
    private val grid: MutableList<MutableList<Int>> =
        MutableList(windowHeight) { MutableList(CHAMBER_WIDTH) { 0 } }

    private fun geAbsoluteHeight(): Long {
        return absoluteHeight
    }

    fun getAbsoluteRockHeight(): Long {
        grid.forEachIndexed { index, rocks ->
            if (rocks.any { it > 0 }) {
                return absoluteHeight + windowHeight - index
            }
        }
        return 0L
    }

    private fun rockHeight(): Int {
        grid.forEachIndexed { index, rocks ->
            if (rocks.any { it > 0 }) {
                return windowHeight - index
            }
        }
        return 0
    }

    fun simulateFalling(iteration: Long, directions: List<Direction>, rockShapes: List<RockShape>) {
        var dirCount = 0
        var rockCount = 0

        (0 until iteration).forEach { _ ->
            val rockShape = rockShapes[rockCount++ % rockShapes.size]
            val rock = Rock(Position(2, windowHeight - this.rockHeight() - rockShape.height() - 3), rockShape)

            var moving = true
            while (moving) {
                val direction = directions[dirCount++ % directions.size]
                tryToMove(rock, direction)
                moving = tryToMove(rock, Direction.D)
                if (!moving) {
                    addRock(rock)
                }
            }
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

                if (element > 0) {
                    val checkedY = pos.y + y
                    val checkedX = pos.x + x
                    if (checkedY !in 0 until windowHeight) return false
                    if (checkedX !in 0 until 7) return false
                    if (grid[checkedY][checkedX] > 0) return false
                }
            }
        }
        return true
    }

    private fun addRock(rock: Rock) {
        val pos = rock.currentPos
        rock.shape.shape.forEachIndexed { y, row ->
            row.forEachIndexed { x, element ->
                if (element > 0) {
                    val checkedY = pos.y + y
                    val checkedX = pos.x + x
                    grid[checkedY][checkedX] = 2
                }
            }
        }
    }

    override fun toString(): String {
        return grid.joinToString("\n") { line ->
            line.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    0 -> "."
                    1 -> "#"
                    2 -> "@"
                    else -> " "
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

enum class RockShape(val shape: List<List<Int>>) {
    A(
        listOf(
            listOf(1, 1, 1, 1)
        )
    ),
    B(
        listOf(
            listOf(0, 1, 0),
            listOf(1, 1, 1),
            listOf(0, 1, 0),
        )
    ),
    C(
        listOf(
            listOf(0, 0, 1),
            listOf(0, 0, 1),
            listOf(1, 1, 1),
        )
    ),
    D(
        listOf(
            listOf(1),
            listOf(1),
            listOf(1),
            listOf(1),
        )
    ),
    E(
        listOf(
            listOf(1, 1),
            listOf(1, 1),
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
                    0 -> "."
                    1 -> "#"
                    2 -> "@"
                    else -> " "
                }
            }
        }
    }
}
