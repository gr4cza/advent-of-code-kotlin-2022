fun main() {
    fun readOperation(line: String): (Long) -> Long {
        val (a, op, b) = line.removePrefix("Operation: new = ").split(" ")
        when (op) {
            "*" -> {
                if (b == "old") {
                    return { it * it }
                } else {
                    return { it * b.toInt() }
                }
            }

            "+" -> {
                if (b == "old") {
                    return { it + it }
                } else {
                    return { it + b.toInt() }
                }
            }

            else -> error("unknown input: $op")
        }
    }

    fun parseInput(input: List<String>): List<Monkey> {
        val monkeys = mutableListOf<Monkey>()
        var monkeyNumber: Int = -1
        var currentItems = mutableListOf<Item>()
        var operation: (Long) -> Long = { it }
        var testValue = 0
        var favoredMonkey = -1
        var hatedMonkey = -1
        input.map {
            it.trim()
        }.forEachIndexed { index, line ->
            if (line.startsWith("Monkey")) {
                monkeyNumber = line.replace(":", "").split(" ").last().toInt()
            }
            if (line.startsWith("Starting items:")) {
                currentItems = line.removePrefix("Starting items: ")
                    .split(", ").map { Item(it.toLong()) }.toMutableList()
            }
            if (line.startsWith("Operation:")) {
                operation = readOperation(line)
            }
            if (line.startsWith("Test:")) {
                testValue = line.split(" ").last().toInt()
            }
            if (line.startsWith("If true:")) {
                favoredMonkey = line.split(" ").last().toInt()
            }
            if (line.startsWith("If false: ")) {
                hatedMonkey = line.split(" ").last().toInt()
            }

            if (line.isBlank() || index == input.size-1) {
                monkeys.add(
                    Monkey(
                        number = monkeyNumber,
                        items = currentItems,
                        operation = operation,
                        testValue = testValue,
                        favoredMonkey = favoredMonkey,
                        hatedMonkey = hatedMonkey,
                    )
                )
            }
        }
        return monkeys
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseInput(input)
        repeat(20) {
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    item.worryLvl = monkey.operation(item.worryLvl) / 3
                    if (item.worryLvl % monkey.testValue == 0L) {
                        monkeys[monkey.favoredMonkey].items.add(item)
                    } else {
                        monkeys[monkey.hatedMonkey].items.add(item)
                    }
                    monkey.inspectCount++
                }
                monkey.items.clear()
            }
        }
        val sortedDescending = monkeys.map { it.inspectCount }.sortedDescending()
        println(sortedDescending)
        return sortedDescending[0] *sortedDescending[1]
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseInput(input)
        val fakeLkkt = monkeys.map { it.testValue }.reduce { acc, i -> acc * i }.toLong()
        repeat(10000) {
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    item.worryLvl = monkey.operation(item.worryLvl) % fakeLkkt
                    if (item.worryLvl % monkey.testValue == 0L) {
                        monkeys[monkey.favoredMonkey].items.add(item)
                    } else {
                        monkeys[monkey.hatedMonkey].items.add(item)
                    }
                    monkey.inspectCount++
                }
                monkey.items.clear()
            }
        }
        val sortedDescending = monkeys.map { it.inspectCount }.sortedDescending()
        println(sortedDescending)
        return sortedDescending[0] *sortedDescending[1]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    println(part1(testInput))
    check(part1(testInput) == 10605L)
    println(part2(testInput))
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

data class Monkey(
    val number: Int,
    val items: MutableList<Item>,
    val operation: (Long) -> Long,
    val testValue: Int,
    val favoredMonkey: Int,
    val hatedMonkey: Int,
    var inspectCount: Long = 0L,
)

data class Item(
    var worryLvl: Long
)
