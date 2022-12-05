fun main() {
    fun readColumnCount(crateLines: List<String>) =
        crateLines.last().split("\\s+".toRegex()).filter { it.isNotBlank() }.maxOf { it.toInt() }

    fun createCrates(columnsCount: Int): List<MutableList<Char>> {
        return (0 until columnsCount).map {
            mutableListOf()
        }
    }

    fun readCrates(crateLines: List<String>): List<ArrayDeque<Char>> {
        val columnCount = readColumnCount(crateLines)
        val crates = createCrates(columnCount)
        crateLines.subList(0, crateLines.size - 1).forEach {
            it.chunked(4).forEachIndexed { index, crateValue ->
                if (crateValue.isNotBlank()) {
                    crates[index].add(crateValue[1])
                }
            }
        }
        return crates.map { ArrayDeque(it) }
    }

    fun splitByEmpty(input: List<String>): Int {
        input.forEachIndexed { index, s ->
            if (s.isBlank()) {
                return index
            }
        }
        return -1
    }

    fun readInstructions(instructions: List<String>): List<Instruction> = instructions
        .map {
            val (_, move, _, from, _, to) = it.split(" ")
            Instruction(move.toInt(), from.toInt() - 1, to.toInt() - 1)
        }

    fun calculateMoves(crates: List<ArrayDeque<Char>>, instructions: List<Instruction>) {
        instructions.forEach {inst ->
            repeat(inst.move) {
                val first = crates[inst.from].removeFirst()
                crates[inst.to].addFirst(first)
            }
        }
    }

    fun calculateMoves2(crates: List<ArrayDeque<Char>>, instructions: List<Instruction>) {
        instructions.forEach {inst ->
        val tempStack = ArrayDeque<Char>()
            repeat(inst.move) {
                val first = crates[inst.from].removeFirst()
                tempStack.addFirst(first)
            }
            repeat(inst.move){
                val first = tempStack.removeFirst()
                crates[inst.to].addFirst(first)
            }
        }
    }

    fun part1(input: List<String>): String {
        val splitValue = splitByEmpty(input)
        val crates = readCrates(input.subList(0, splitValue))
        val instructions = readInstructions(input.subList(splitValue + 1, input.size))
        calculateMoves(crates, instructions)
        return crates.map { it.first() }.joinToString("")
    }

    fun part2(input: List<String>): String {
        val splitValue = splitByEmpty(input)
        val crates = readCrates(input.subList(0, splitValue))
        val instructions = readInstructions(input.subList(splitValue + 1, input.size))
        calculateMoves2(crates, instructions)
        return crates.map { it.first() }.joinToString("")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    println(part1(testInput))
    check(part1(testInput) == "CMZ")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

data class Instruction(
    val move: Int,
    val from: Int,
    val to: Int,
)

private operator fun <E> List<E>.component6(): String {
    return this[5].toString()
}
