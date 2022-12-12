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
    var heads = mutableListOf(startPos)
    heatMap[startPos] = 1

    while (true) {
        val newHeads = mutableListOf<Position>()
        heads.forEach { pos ->
            checkWhereToStep(pos, this, heatMap).forEach { direction ->
                val currentStepCount = heatMap[pos]
                val newPos: Position =
                    when (direction) {
                        Direction.U -> Position(x = pos.x, y = pos.y - 1)
                        Direction.D -> Position(x = pos.x, y = pos.y + 1)
                        Direction.L -> Position(x = pos.x - 1, y = pos.y)
                        Direction.R -> Position(x = pos.x + 1, y = pos.y)
                    }
                if (newPos == endPos) {
                    return heatMap[pos]
                }
                newHeads.add(newPos)
                heatMap[newPos] = currentStepCount + 1
            }
        }

        if (newHeads.isEmpty()) {
            return Int.MAX_VALUE
        }
        heads = newHeads
    }
}

fun checkWhereToStep(pos: Position, map: List<List<Char>>, heatMap: List<List<Int>>): List<Direction> {
    val possibleDirs = mutableListOf<Direction>()
    if (pos.y != 0 && (checkStep(map[pos], map[pos.y - 1][pos.x])) && heatMap[pos.y - 1][pos.x] == 0) {
        possibleDirs.add(Direction.U)
    }
    if (pos.y != map.size - 1 && (checkStep(map[pos], map[pos.y + 1][pos.x])) && heatMap[pos.y + 1][pos.x] == 0) {
        possibleDirs.add(Direction.D)
    }
    if (pos.x != 0 && (checkStep(map[pos], map[pos.y][pos.x - 1])) && heatMap[pos.y][pos.x - 1] == 0) {
        possibleDirs.add(Direction.L)
    }
    if (pos.x != map.first().size - 1 &&
        (checkStep(map[pos], map[pos.y][pos.x + 1])) && heatMap[pos.y][pos.x + 1] == 0
    ) {
        possibleDirs.add(Direction.R)
    }
    return possibleDirs
}

private fun checkStep(start: Char, end: Char): Boolean {
    return start >= end || start + 1 == end
}

private operator fun <E> List<List<E>>.get(pos: Position): E {
    return this[pos.y][pos.x]
}

private operator fun <E> List<MutableList<E>>.set(pos: Position, element: E) {
    this[pos.y][pos.x] = element
}
