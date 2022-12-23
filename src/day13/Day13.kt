package day13

import readInput

fun readToPacket(chars: String): BasePacket {
    if (chars.isEmpty()) {
        return PacketList(mutableListOf())
    }
    if (chars.toIntOrNull() != null) {
        return Packet(chars.toInt())
    }

    return PacketList(readNestedList(chars))
}


fun readNestedList(chars: String): List<BasePacket> {
    val substring = chars.substring(1, chars.length - 1)
    return buildList {
        var index = 0
        while (index < substring.length) {
            if (substring[index] == '[') {
                val parenthesisEnd = findParenthesisEnd(index, substring)
                add(readToPacket(substring.substring(index, parenthesisEnd)))
                index = parenthesisEnd + 1
            } else {
                var intEnd = substring.indexOf(',', startIndex = index + 1)
                if (intEnd == -1) {
                    intEnd = substring.length
                }
                add(readToPacket(substring.substring(index, intEnd)))
                index = intEnd + 1
            }
        }
    }
}

fun findParenthesisEnd(start: Int, substring: String): Int {
    var parenthesisCount = 1
    var index = start + 1
    while (parenthesisCount != 0) {
        if (substring[index] == '[') {
            parenthesisCount++
        }
        if (substring[index] == ']') {
            parenthesisCount--
        }
        index++
    }
    return index
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.windowed(2, 3).map { (first, second) ->
            readToPacket(first) to readToPacket(second)
        }.mapIndexed { i, (first, second) ->
            if (first < second) {
                i + 1
            } else {
                0
            }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val additionalPacket1 = readToPacket("[[2]]")
        val additionalPacket2 = readToPacket("[[6]]")
        val sorted = input.filter {
            it.isNotBlank()
        }.map {
            readToPacket(it)
        }.toMutableList().apply {
            add(additionalPacket1)
            add(additionalPacket2)
        }.sorted()
        return (sorted.indexOf(additionalPacket1) + 1) * (sorted.indexOf(additionalPacket2) + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day13/Day13_test")
    println(part1(testInput))
    check(part1(testInput) == 13)

    val input = readInput("day13/Day13")
    println(part1(input))
    println(part2(input))
}

sealed class BasePacket : Comparable<BasePacket>
class PacketList(
    private val subLists: List<BasePacket>
) : BasePacket() {
    override fun compareTo(other: BasePacket): Int {
        return when (other) {
            is PacketList -> {
                this.subLists.zip(other.subLists).forEach { (first, second) ->
                    val compareTo = first.compareTo(second)
                    if (compareTo != 0) {
                        return compareTo
                    }
                }
                return subLists.size.compareTo(other.subLists.size)
            }

            is Packet -> {
                this.compareTo(PacketList(listOf(other)))
            }
        }
    }

    override fun toString(): String = subLists.toString()
}


class Packet(
    private val num: Int
) : BasePacket() {
    override fun compareTo(other: BasePacket): Int {
        return when (other) {
            is PacketList -> PacketList(listOf(this)).compareTo(other)

            is Packet -> {
                this.num.compareTo(other.num)
            }
        }
    }

    override fun toString(): String = num.toString()
}
