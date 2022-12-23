package day20

import readInput

fun main() {
    fun parse(input: List<String>): List<Number> {
        return input.mapIndexed { i, line -> Number(line.toInt(), i) }
    }

    fun arrangeNumbers(numbers: List<Number>): MutableList<Number> {
        val order = numbers.toMutableList()

        numbers.forEach {currentNumber ->
            val currentPos = order.indexOf(currentNumber)
            order.removeAt(currentPos)
            order.add((currentPos + currentNumber.value).mod(order.size), currentNumber)
        }

        return order
    }

    fun arrangeNumbers(numbers: List<BigNumber>, times: Int): MutableList<BigNumber> {
        val order = numbers.toMutableList()

        repeat(times) {
            numbers.forEach {currentNumber ->
                val currentPos = order.indexOf(currentNumber)
                order.removeAt(currentPos)
                order.add((currentPos + currentNumber.value).mod(order.size), currentNumber)
            }
        }

        return order
    }

    fun part1(input: List<String>): Int {
        val numbers = parse(input)
        val rearrangedNumbers = arrangeNumbers(numbers)
        val zeroIndex = rearrangedNumbers.indexOfFirst { it.value == 0 }
        return rearrangedNumbers[(zeroIndex + 1000) % rearrangedNumbers.size].value +
            rearrangedNumbers[(zeroIndex + 2000) % rearrangedNumbers.size].value +
            rearrangedNumbers[(zeroIndex + 3000) % rearrangedNumbers.size].value
    }

    fun part2(input: List<String>): Long {
        val numbers = parse(input).map { BigNumber(it.value * 811589153L, it.idx) }
        val rearrangedNumbers = arrangeNumbers(numbers, 10)
        val zeroIndex = rearrangedNumbers.indexOfFirst { it.value == 0L }
        return rearrangedNumbers[(zeroIndex + 1000) % rearrangedNumbers.size].value +
            rearrangedNumbers[(zeroIndex + 2000) % rearrangedNumbers.size].value +
            rearrangedNumbers[(zeroIndex + 3000) % rearrangedNumbers.size].value
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day20/Day20_test")
    val input = readInput("day20/Day20")

    check((part1(testInput)).also { println(it) } == 3)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 1623178306L)
    println(part2(input))
}

data class Number(
    val value: Int,
    val idx: Int
)

data class BigNumber(
    val value: Long,
    val idx: Int
)
