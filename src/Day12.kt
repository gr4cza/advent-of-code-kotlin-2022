fun main() {
    fun parseInput(input: List<String>): List<List<Char>> {
        return input.map { line ->
            line.chunked(1).map {
                when (val char = it.toCharArray().first()) {
                    'S' -> 'a'
                    'E' -> 'z'
                    else -> char
                }
            }
        }
    }

    fun findPositions(map: List<String>): Pair<Position, Position> {
        var startPos = Position(0, 0)
        var endPos = Position(0, 0)
        map.forEachIndexed { y, line ->
            line.forEachIndexed { x, pos ->
                if (pos == 'S') {
                    startPos = Position(x, y)
                } else if (pos == 'E') {
                    endPos = Position(x, y)
                }
            }

        }
        return (startPos to endPos)
    }

    fun part1(input: List<String>): Int {
        val (startPos, endPos) = findPositions(input)
        val map = parseInput(input)
        return map.findRoute(startPos, endPos)
    }

    fun part2(input: List<String>): Int {
        val (_, endPos) = findPositions(input)
        val map = parseInput(input)
        return map.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                c to Position(x, y)
            }
        }.flatten()
            .filter { it.first == 'a' }
            .map { it.second }.minOf { p ->
                map.findRoute(p, endPos)
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    println(part1(testInput))
    check(part1(testInput) == 31)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private fun List<List<Char>>.findRoute(startPos: Position, endPos: Position): Int {
    val heatMap = List(this.size) { MutableList(this.first().size) { 0 } }
    var heads = ArrayDeque(listOf(startPos))
    heatMap[startPos] = 1

    while (heads.isNotEmpty()) {
        heads.removeFirst().let { pos ->
            checkWhereToStep(pos, this, heatMap).forEach { direction ->
                val newPos = pos.newPosition(direction)
                if (newPos == endPos) {
                    return heatMap[pos]
                }
                heads.add(newPos)
                heatMap[newPos] = heatMap[pos] + 1
            }
        }
    }
    return Int.MAX_VALUE
}

fun checkWhereToStep(pos: Position, map: List<List<Char>>, heatMap: List<List<Int>>): List<Direction> =
    Direction.values().filter { dir ->
        val newPos = pos.newPosition(dir)
        when (dir) {
            Direction.U -> pos.y != 0
            Direction.D -> pos.y != map.size - 1
            Direction.L -> pos.x != 0
            Direction.R -> pos.x != map.first().size - 1
        } && (checkStep(map[pos], map[newPos])) && heatMap[newPos] == 0
    }.toList()

private fun checkStep(start: Char, end: Char): Boolean = start >= (end - 1)

operator fun <E> List<List<E>>.get(pos: Position): E {
    return this[pos.y][pos.x]
}

operator fun <E> List<MutableList<E>>.set(pos: Position, element: E) {
    this[pos.y][pos.x] = element
}
