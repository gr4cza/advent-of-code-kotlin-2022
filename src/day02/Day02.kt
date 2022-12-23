@file:Suppress("MagicNumber")

package day02

import day02.Type.*
import readInput

enum class Type(val value: Int) {
    ROCK(1), PAPER(2), SCISSOR(3)
}

fun main() {

    fun translate(s: String): Type = when (s) {
        "A", "X" -> ROCK
        "B", "Y" -> PAPER
        "C", "Z" -> SCISSOR
        else -> error("")
    }

    fun winedPrice(game: Pair<Type, Type>): Int = when (game) {
        ROCK to PAPER -> 6
        PAPER to SCISSOR -> 6
        SCISSOR to ROCK -> 6
        ROCK to SCISSOR -> 0
        PAPER to ROCK -> 0
        SCISSOR to PAPER -> 0
        else -> 3
    }


    fun calculateScore(it: Pair<Type, Type>) = it.second.value + winedPrice(it)

    fun cleanUpInput(input: List<String>) = input.map { it.split(" ") }
        .map { translate(it[0]) to translate(it[1]) }

    fun part1(input: List<String>): Int = cleanUpInput(input).sumOf {
        calculateScore(it)
    }

    fun calculateGame(game: Pair<Type, Type>): Type =
        when (game.second) {
            ROCK -> {
                when (game.first) {
                    ROCK -> SCISSOR
                    PAPER -> ROCK
                    SCISSOR -> PAPER
                }
            }

            PAPER -> game.first
            SCISSOR -> {
                when (game.first) {
                    ROCK -> PAPER
                    PAPER -> SCISSOR
                    SCISSOR -> ROCK
                }

            }
        }

    fun part2(input: List<String>): Int = cleanUpInput(input).map {
        it.first to calculateGame(it)
    }.sumOf {
        calculateScore(it)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02/Day02_test")
    println(part1(testInput))
    check(part1(testInput) == 15)

    val input = readInput("day02/Day02")
    println(part1(input))
    println(part2(input))
}
