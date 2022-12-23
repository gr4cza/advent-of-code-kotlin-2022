package day04

import readInput

fun main() {
    fun String.toRange(): Pair<Int, Int> {
        val split = this.split("-")
        return split.first().toInt() to split.last().toInt()
    }

    fun parse(it: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val (firstRange, secondRange) = it.split(",")
        return firstRange.toRange() to secondRange.toRange()
    }

    fun compareContaining(sectionPairs: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean {
        return (sectionPairs.first.first .. sectionPairs.first.second).toSet().containsAll(
            (sectionPairs.second.first .. sectionPairs.second.second).toSet()) ||
            (sectionPairs.second.first .. sectionPairs.second.second).toSet().containsAll(
                (sectionPairs.first.first .. sectionPairs.first.second).toSet())
    }

    fun compareOverlapping(sectionPairs: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean {
        val intersect = (sectionPairs.first.first..sectionPairs.first.second).intersect(
            sectionPairs.second.first..sectionPairs.second.second
        )
        return intersect.isNotEmpty()
    }

    fun part1(input: List<String>): Int {
        return input.map {
            parse(it)
        }.count {
            compareContaining(it)
        }
    }

    fun part2(input: List<String>): Int {
        return input.map {
            parse(it)
        }.count {
            compareOverlapping(it)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day04/Day04_test")
    println(part1(testInput))
    check(part1(testInput) == 2)

    val input = readInput("day04/Day04")
    println(part1(input))
    println(part2(input))
}
