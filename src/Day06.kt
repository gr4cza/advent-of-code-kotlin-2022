fun main() {
    fun findMessage(input: List<String>, messageSize: Int): Int {
        input.first().chunked(1).windowed(messageSize).forEachIndexed() { index, strings ->
            if (strings.toSet().size == messageSize) {
                return index + messageSize
            }
        }
        error("No message")
    }

    fun part1(input: List<String>): Int {
        return findMessage(input, 4)
    }

    fun part2(input: List<String>): Int {
        return findMessage(input, 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    println(part1(testInput))
    check(part1(testInput) == 5)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
