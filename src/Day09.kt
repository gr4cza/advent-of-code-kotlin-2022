import kotlin.math.abs

fun main() {
    fun parseMoves(input: List<String>) = input.map {
        val (dir, dist) = it.split(" ")
        Move(Direction.valueOf(dir), dist.toInt())
    }

    fun distY(tail: Position, head: Position): Int {
        return abs(tail.y - head.y)
    }

    fun distX(tail: Position, head: Position): Int {
        return abs(tail.x - head.x)
    }

    fun moveDiagonally(head: Position, tail: Position) {
        if (tail.y > head.y) tail.y-- else tail.y++
        if (tail.x > head.x) tail.x-- else tail.x++
    }

    fun calculateNewTail(head: Position, tail: Position) {
        // Same pos
        if (head == tail || ((distX(tail, head) == 1 && distY(tail, head) == 1))) {
            return
        } else if (head.y == tail.y && distX(tail, head) == 2) {
            if (head.x > tail.x) tail.x++ else tail.x--
        } else if (head.x == tail.x && distY(tail, head) == 2) {
            if (head.y > tail.y) tail.y++ else tail.y--
        } else if((distX(tail, head) == 2 || distY(tail, head) == 2)) {
            moveDiagonally(head, tail)
        }
    }

    fun step(dir: Direction, head: Position) {
        when (dir) {
            Direction.U -> head.y += 1
            Direction.D -> head.y -= 1
            Direction.L -> head.x -= 1
            Direction.R -> head.x += 1
        }
    }

    fun part1(input: List<String>): Int {
        val moves = parseMoves(input)
        val tailPositions = mutableListOf<Position>()
        val head = Position(0, 0)
        val tail = Position(0, 0)
        tailPositions.add(tail)
        moves.forEach { (dir, dist) ->
            repeat(dist) {
                step(dir, head)
                calculateNewTail(head, tail)
                tailPositions.add(tail.copy())
            }
        }

        return tailPositions.toSet().size
    }

    fun part2(input: List<String>): Int {
        val moves = parseMoves(input)
        val tailPositions = mutableListOf<Position>()
        val head = Position(0, 0)
        val knots = MutableList(9){(Position(0, 0))}
        tailPositions.add(knots.last())
        moves.forEach { (dir, dist) ->
            repeat(dist) {
                step(dir, head)
                val listOf = listOf(head, *knots.toTypedArray())
                listOf.windowed(2).forEach {
                    calculateNewTail(it.first(), it.last())
                }
                tailPositions.add(knots.last().copy())
            }
        }

        return tailPositions.toSet().size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    println(part1(testInput))
    check(part1(testInput) == 13)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}


data class Move(
    val direction: Direction,
    val distance: Int
)

enum class Direction {
    U, D, L, R,
}

data class Position(
    var x: Int,
    var y: Int,
)
