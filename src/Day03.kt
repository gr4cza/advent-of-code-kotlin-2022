fun main() {
    fun score(it: List<String>): Int = when (val char = it[0][0]) {
        in 'a'..'z' -> char - 'a' + 1
        in 'A'..'Z' -> char - 'A' + 27
        else -> error("")
    }

    fun cleanUpInput(input: List<String>) = input.map { it.chunked(1) }

    fun part1(input: List<String>): Int {
        return cleanUpInput(input)
            .map { it.chunked(it.size / 2) }
            .map {
                it[0].intersect(it[1].toSet()).toList()
            }.sumOf { score(it) }
    }

    fun part2(input: List<String>): Int {
        return cleanUpInput(input)
            .chunked(3)
            .map {
                it[0].intersect(it[1].toSet()).intersect(it[2].toSet()).toList()
            }.sumOf { score(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    println(part1(testInput))
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
