package day03

import readInput

fun main() {
    fun score(item: Char): Int = when (item) {
        in 'a'..'z' -> item - 'a' + 1
        in 'A'..'Z' -> item - 'A' + 27
        else -> error("")
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.chunked(it.length / 2) }
            .map {
                it[0].toSet() intersect it[1].toSet()
            }.sumOf { score(it.single()) }
    }

    fun part2(input: List<String>): Int {
        return input
            .chunked(3)
            .map {
                it[0].toSet() intersect it[1].toSet() intersect it[2].toSet()
            }.sumOf { score(it.single()) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/Day03_test")
    println(part1(testInput))
    check(part1(testInput) == 157)

    val input = readInput("day03/Day03")
    println(part1(input))
    println(part2(input))
}
