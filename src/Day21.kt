fun main() {
    fun parse(input: List<String>): List<Monkey> {
        val monkeys = input.map { line ->
            val split = line.split(": ")
            Monkey(
                name = split.first(),
                yell = when {
                    (split.last().toIntOrNull() != null) -> { _ ->
                        split.last().toLong()
                    }

                    else -> {
                        val action = split.last().split(" ")
                        when (action[1]) {
                            "*" -> { monkeys ->
                                monkeys.first { it.name == action[0] }
                                    .yell(monkeys) * monkeys.first { it.name == action[2] }.yell(monkeys)
                            }

                            "+" -> { monkeys ->
                                monkeys.first { it.name == action[0] }
                                    .yell(monkeys) + monkeys.first { it.name == action[2] }.yell(monkeys)
                            }

                            "-" -> { monkeys ->
                                monkeys.first { it.name == action[0] }
                                    .yell(monkeys) - monkeys.first { it.name == action[2] }.yell(monkeys)
                            }

                            "/" -> { monkeys ->
                                monkeys.first { it.name == action[0] }
                                    .yell(monkeys) / monkeys.first { it.name == action[2] }
                                    .yell(monkeys)
                            }

                            else -> error("Wrong input")
                        }
                    }
                }
            )
        }
        return monkeys
    }

    fun part1(input: List<String>): Long {
        val monkeys = parse(input)
        val rootMonkey = monkeys.find { monkey -> monkey.name == "root" }
        return rootMonkey?.yell?.let { it(monkeys) } ?: 0
    }

    fun alterInput(input: List<String>): List<String> {
        val alteredInput = input.toMutableList()
        alteredInput.removeIf { it.contains("humn:") }
        val rootLine = alteredInput.first { it.contains("root:") }
        alteredInput.remove(rootLine)
        val newRootLine = rootLine.replace("+", "-")
        alteredInput.add(newRootLine)
        return alteredInput
    }

    fun part2(input: List<String>): Long {
        val alterInput = alterInput(input)
        val monkeys = parse(alterInput)
        val rootMonkey = monkeys.find { monkey -> monkey.name == "root" }
        var i = 0L
        var delta = 1_000_000_000_000L
        while (true) {
            val me = Monkey("humn") { _ -> i }
            val root = rootMonkey?.yell?.let { it(monkeys + listOf(me)) } ?: 0
            println("$i:$root")
            if (root == 0L) {
                return i
            }
            if (root < 0) {
                i -= delta
                delta /= 10
            }
            i += delta
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")

    check((part1(testInput)).also { println(it) } == 152L)
    check((part1(input)).also { println(it) } == 38731621732448L)
//    check(part2(testInput).also { println(it) } == 301L)
    println(part2(input))
}

data class Monkey(
    val name: String,
    val yell: (List<Monkey>) -> Long
)
