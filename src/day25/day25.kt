package day25

import readInput
import kotlin.math.pow

fun main() {
    fun fromSnafu(line: String) = line.mapIndexed { index, digit ->
        5.0.pow(line.lastIndex - index) * when (digit) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> error("wrong input")
        }
    }.sum().toLong()

    fun parse(input: List<String>): Long {
        return input.sumOf { line ->
            fromSnafu(line)
        }
    }

    fun part1(input: List<String>): String {
        val sum = parse(input)
        println("sum:  $sum")
        println("snafu:${fromSnafu(sum.toSnafu())}")
        return sum.toSnafu()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day25/Day25_test")
    val input = readInput("day25/Day25")

    check((part1(testInput)).also { println(it) } == "2=-1=0")
    println(part1(input))
    check(part2(testInput).also { println(it) } == 1)
    println(part2(input))
}

private fun Long.toSnafu(): String {
    if (this == 0L) return ""
    return when (this.mod(5)) {
        0 -> (this / 5).toSnafu() + "0"
        1 -> (this / 5).toSnafu() + "1"
        2 -> (this / 5).toSnafu() + "2"
        3 -> (this / 5 + 1).toSnafu() + "="
        4 -> (this / 5 + 1).toSnafu() + "-"
        else -> error("wrong number")
    }
}
