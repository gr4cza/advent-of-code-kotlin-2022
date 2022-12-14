import Direction.*

val sandStartPos = Position(500, 0)
fun main() {
    fun parseLines(input: List<String>): List<RockLine> {
        return input.map { line ->
            RockLine(line.split(" -> ").map { coords ->
                val (x, y) = coords.split(",").map { it.toInt() }
                Position(x, y)
            })
        }
    }

    fun findEdges(rockLines: List<RockLine>): Edges {
        val xValues = rockLines.map { rockLine ->
            rockLine.linePoints.map {
                it.x
            }
        }.flatten()
        val yValues = rockLines.map { rockLine ->
            rockLine.linePoints.map {
                it.y
            }
        }.flatten()
        return Edges(
            Position(xValues.min(), minOf(yValues.min(), 0)),
            Position(xValues.max(), yValues.max()),
        )
    }

    fun addLines(grid: OffsetGrid, rockLines: List<RockLine>) {
        rockLines.forEach { rockLine ->
            rockLine.linePoints.windowed(2).forEach { (start, end) ->
                if (start.x == end.x) {
                    for (i in minOf(start.y, end.y)..maxOf(start.y, end.y)) {
                        grid[Position(start.x, i)] = 1
                    }
                } else if (start.y == end.y) {
                    for (i in minOf(start.x, end.x)..maxOf(start.x, end.x)) {
                        grid[Position(i, start.y)] = 1
                    }
                }
            }
        }

    }

    fun addSand(grid: OffsetGrid): Int {
        var sandCount = 0
        while (grid[sandStartPos] == 0) {
            try {
                var currentSandPos = sandStartPos

                var inMotion = true
                while (inMotion) {
                    if (grid[currentSandPos.newPosition(D)] == 0) {
                        currentSandPos = currentSandPos.newPosition(D)
                    } else if (grid[currentSandPos.newPosition(D).newPosition(L)] == 0) {
                        currentSandPos = currentSandPos.newPosition(D).newPosition(L)
                    } else if (grid[currentSandPos.newPosition(D).newPosition(R)] == 0) {
                        currentSandPos = currentSandPos.newPosition(D).newPosition(R)
                    } else {
                        grid[currentSandPos] = 2
                        inMotion = false
                    }
                }

                sandCount++
            } catch (e: IndexOutOfBoundsException) {
                break
            }
        }
        return sandCount
    }

    fun part1(input: List<String>): Int {
        val rockLines = parseLines(input)
        val grid = OffsetGrid(findEdges(rockLines))
        addLines(grid, rockLines)
        return addSand(grid)
    }

    fun part2(input: List<String>): Int {
        val rockLines = parseLines(input)
        val edges = findEdges(rockLines)
        val newRockLines = rockLines.toMutableList()
        newRockLines.add(
            RockLine(
                listOf(
                    Position(0, edges.endPos.y + 2),
                    Position(1000, edges.endPos.y + 2),
                )
            )
        )
        val newEdges = findEdges(newRockLines)
        val grid = OffsetGrid(newEdges)
        addLines(grid, newRockLines)

        return addSand(grid)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput).also { println(it) } == 24)

    val input = readInput("Day14")
    println(part1(input))
    check(part2(testInput).also { println(it) } == 93)
    println(part2(input))
}

class OffsetGrid(
    edges: Edges
) {
    private val offsetX: Int = edges.startPos.x
    private val offsetY: Int = edges.startPos.y
    private val grid: List<MutableList<Int>>

    init {
        grid = List(edges.endPos.y - edges.startPos.y + 1) { MutableList(edges.endPos.x - edges.startPos.x + 1) { 0 } }
    }

    override fun toString(): String {
        return grid.joinToString("\n") { line ->
            line.joinToString(",", prefix = "[", postfix = "]")
        }
    }

    operator fun get(position: Position): Int {
        return grid[position.y - offsetY][position.x - offsetX]
    }

    operator fun set(position: Position, value: Int) {
        grid[position.y - offsetY][position.x - offsetX] = value
    }
}

data class RockLine(
    val linePoints: List<Position>
)

data class Edges(
    val startPos: Position,
    val endPos: Position,
)
