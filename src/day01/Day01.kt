package day01

import readInput

fun main() {
    fun getListOfCalories(input: List<String>): List<Int> = mutableListOf<Int>().apply {
        var subSum = 0
        input.forEach {
            if (it.isBlank()) {
                add(subSum)
                subSum = 0
            } else {
                subSum += it.toInt()
            }
        }
    }

    fun part1(input: List<String>): Int = getListOfCalories(input).max()

    fun part2(input: List<String>): Int = getListOfCalories(input).sortedDescending().take(3).sum()

    val input = readInput("day01/Day01")
    println(part1(input))
    println(part2(input))
}
