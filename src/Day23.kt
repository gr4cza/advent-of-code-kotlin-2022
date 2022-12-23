import kotlin.math.abs

fun main() {
    fun parse(input: List<String>): ElfMap {
        return ElfMap(
            elfs = input.mapIndexed { y, row ->
                row.mapIndexedNotNull { x, cell ->
                    if (cell == '#') {
                        Elf(Position(x, y))
                    } else {
                        null
                    }
                }
            }.flatten()
        )
    }

    fun getProposedMoves(
        elfMap: ElfMap,
        currentPositions: List<Position>,
        dirs: ArrayDeque<Direction>,
    ) = elfMap.elfs.map { elf ->
        if (elf.countNeighbours(currentPositions) != 0) {
            dirs.forEach { dir ->
                when (dir) {
                    Direction.U -> {
                        if (listOf(
                                elf.currentPos.newPosition(Direction.U) in currentPositions,
                                elf.currentPos.newPosition(Direction.U)
                                    .newPosition(Direction.R) in currentPositions,
                                elf.currentPos.newPosition(Direction.U)
                                    .newPosition(Direction.L) in currentPositions,
                            ).none { it }
                        ) {
                            return@map elf to elf.currentPos.newPosition(Direction.U)
                        }
                    }

                    Direction.D -> {
                        if (listOf(
                                elf.currentPos.newPosition(Direction.D) in currentPositions,
                                elf.currentPos.newPosition(Direction.D)
                                    .newPosition(Direction.R) in currentPositions,
                                elf.currentPos.newPosition(Direction.D)
                                    .newPosition(Direction.L) in currentPositions,
                            ).none { it }
                        ) {
                            return@map elf to elf.currentPos.newPosition(Direction.D)
                        }
                    }

                    Direction.L -> {
                        if (listOf(
                                elf.currentPos.newPosition(Direction.L) in currentPositions,
                                elf.currentPos.newPosition(Direction.L)
                                    .newPosition(Direction.U) in currentPositions,
                                elf.currentPos.newPosition(Direction.L)
                                    .newPosition(Direction.D) in currentPositions,
                            ).none { it }
                        ) {
                            return@map elf to elf.currentPos.newPosition(Direction.L)
                        }
                    }

                    Direction.R -> {
                        if (listOf(
                                elf.currentPos.newPosition(Direction.R) in currentPositions,
                                elf.currentPos.newPosition(Direction.R)
                                    .newPosition(Direction.U) in currentPositions,
                                elf.currentPos.newPosition(Direction.R)
                                    .newPosition(Direction.D) in currentPositions,
                            ).none { it }
                        ) {
                            return@map elf to elf.currentPos.newPosition(Direction.R)
                        }
                    }
                }
            }
            return@map elf to elf.currentPos
        } else {
            return@map elf to elf.currentPos
        }
    }

    fun simulate(elfMap: ElfMap, times: Int, directions: ArrayDeque<Direction>) {
        repeat(times) {
            val currentPositions = elfMap.elfs.map { it.currentPos }
            val proposedMoves: List<Pair<Elf, Position>> = getProposedMoves(elfMap, currentPositions, directions)
            proposedMoves.forEach { (elf, newPos) ->
                if (proposedMoves.count { it.second == newPos } == 1) {
                    elf.currentPos = newPos
                }
            }

            directions.removeFirst().also { directions.addLast(it) }
        }
    }

    fun part1(input: List<String>): Int {
        val elfMap = parse(input)
        val times = 10
        val directions = ArrayDeque(listOf(Direction.U, Direction.D, Direction.L, Direction.R))
        simulate(elfMap, times, directions)
        return elfMap.convertToMap().flatten().count { !it }
    }

    fun part2(input: List<String>): Int {
        val elfMap = parse(input)
        var previousMap = elfMap.hashCode()
        var i = 0
        val directions = ArrayDeque(listOf(Direction.U, Direction.D, Direction.L, Direction.R))
        while (true) {
            simulate(elfMap, 1, directions)
            if (previousMap == elfMap.hashCode()) {
                return i+1
            } else {
                i++
                previousMap = elfMap.hashCode()
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    val input = readInput("Day23")

    check((part1(testInput)).also { println(it) } == 110)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 20)
    println(part2(input))
}


data class ElfMap(
    val elfs: List<Elf>,
) {
    fun convertToMap(): List<List<Boolean>> {
        val (startPos, endPos) = findEdges()
        val elfMap = List(abs(startPos.y - endPos.y) + 1) { MutableList(abs(startPos.x - endPos.x) + 1) { false } }
        elfs.forEach {
            elfMap[it.currentPos.copy(x = it.currentPos.x - startPos.x, y = it.currentPos.y - startPos.y)] = true
        }
        return elfMap
    }

    private fun findEdges(): Edges {
        val xValues = elfs.map { it.currentPos.x }
        val yValues = elfs.map { it.currentPos.y }
        return Edges(
            Position(xValues.min(), minOf(yValues.min(), 0)),
            Position(xValues.max(), yValues.max()),
        )
    }

    override fun toString(): String {

        return buildString {
            append(findEdges().toString())
            append("\n")
            append(convertToMap().joinToString("\n") { line ->
                line.joinToString("") {
                    when (it) {
                        false -> "."
                        true -> "#"
                    }
                }
            })
        }
    }
}

data class Elf(
    var currentPos: Position,
) {
    fun countNeighbours(currentPositions: List<Position>): Int {
        return listOf(
            currentPos.newPosition(Direction.U) in currentPositions,
            currentPos.newPosition(Direction.R) in currentPositions,
            currentPos.newPosition(Direction.D) in currentPositions,
            currentPos.newPosition(Direction.L) in currentPositions,
            currentPos.newPosition(Direction.U).newPosition(Direction.R) in currentPositions,
            currentPos.newPosition(Direction.R).newPosition(Direction.D) in currentPositions,
            currentPos.newPosition(Direction.D).newPosition(Direction.L) in currentPositions,
            currentPos.newPosition(Direction.L).newPosition(Direction.U) in currentPositions,
        ).count { it }
    }
}
