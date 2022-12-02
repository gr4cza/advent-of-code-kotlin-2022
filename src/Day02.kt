@file:Suppress("MagicNumber")
enum class Type(val value: Int) {
    ROCK(1), PAPER(2), SCISSOR(3)
}

fun main() {

    fun translate(s: String): Type = when (s) {
        "A", "X" -> Type.ROCK
        "B", "Y" -> Type.PAPER
        "C", "Z" -> Type.SCISSOR
        else -> error("")
    }

    fun winedPrice(game: Pair<Type, Type>): Int = when (game) {
        Pair(Type.ROCK, Type.PAPER) -> 6
        Pair(Type.PAPER, Type.SCISSOR) -> 6
        Pair(Type.SCISSOR, Type.ROCK) -> 6
        Pair(Type.ROCK, Type.SCISSOR) -> 0
        Pair(Type.PAPER, Type.ROCK) -> 0
        Pair(Type.SCISSOR, Type.PAPER) -> 0
        else -> 3
    }


    fun calculateScore(it: Pair<Type, Type>) = it.second.value + winedPrice(it)

    fun cleanUpInput(input: List<String>) = input.map { it.split(" ") }
        .map { Pair(translate(it[0]), translate(it[1])) }

    fun part1(input: List<String>): Int = cleanUpInput(input).sumOf {
        calculateScore(it)
    }

    fun calculateGame(game: Pair<Type, Type>): Type =
        when (game.second) {
            Type.ROCK -> {
                when (game.first) {
                    Type.ROCK -> Type.SCISSOR
                    Type.PAPER -> Type.ROCK
                    Type.SCISSOR -> Type.PAPER
                }
            }

            Type.PAPER -> game.first
            Type.SCISSOR -> {
                when (game.first) {
                    Type.ROCK -> Type.PAPER
                    Type.PAPER -> Type.SCISSOR
                    Type.SCISSOR -> Type.ROCK
                }

            }
        }

    fun part2(input: List<String>): Int = cleanUpInput(input).map {
        Pair(it.first, calculateGame(it))
    }.sumOf {
        calculateScore(it)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    println(part1(testInput))
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
