fun main() {
    fun parse(input: List<String>): List<Valve> {
        val split = input.map {
            val (valves, connections) = it.split(";")
            valves to connections
        }
        val valveList = split.map { (valves, _) ->
            val (_, name, _, _, rate) = valves.split(" ")
            Valve(name, rate.split("=").last().toInt())
        }
        split.map { (_, connections) ->
            when {
                connections.startsWith(" tunnels lead to valves ") -> {
                    connections
                        .removePrefix(" tunnels lead to valves ").split(", ")
                }

                else -> {
                    listOf(
                        connections
                            .removePrefix(" tunnel leads to valve ")
                    )
                }
            }
        }.forEachIndexed { index, names ->
            names.forEach { n ->
                valveList[index].connections.add(valveList.first { it.name == n })
            }
        }
        return valveList
    }

    fun part1(input: List<String>): Int {
        val valves = parse(input)
        var remainingTime = 30
        var currentValve = valves.first { it.name == "AA" }

        return currentValve.calculateValues(
            remainingTime = remainingTime,
            closedValue = valves,
            openedValve = emptyList(),
            currentRoute = "",
        )
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")

    check((part1(testInput)).also { println(it) } == 1651)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 1)
    println(part2(input))
}

private fun Valve.calculateValues(
    remainingTime: Int,
    closedValue: List<Valve>,
    openedValve: List<Valve>,
    currentRoute: String,
    previous: Valve? = null
): Int {
    val currentRouteName = currentRoute + this.name

    if (remainingTime <= 0 || closedValue.none { it.flowRate > 0 }) return 0

    val possibleConnections = if (this.connections.size == 1) {
        this.connections
    } else {
        this.connections.filter { it != previous }
    }
    return if (this.flowRate > 0 && closedValue.contains(this)) {
        val newClosedValves = closedValue.toMutableList()
        newClosedValves.remove(this)
        val newOpenedValves = openedValve.toMutableList()
        newOpenedValves.add(this)
        val value = this.flowRate * (remainingTime - 1)
        val downStream1 = possibleConnections.maxOf {
            it.calculateValues(remainingTime - 2, newClosedValves, newOpenedValves, currentRouteName, this)
        }
        val downStream2 = possibleConnections.maxOf {
            it.calculateValues(remainingTime - 1, closedValue, openedValve, currentRouteName, this)
        }
        maxOf(value + downStream1, downStream2)
    } else {
        val downStream = possibleConnections.maxOf {
            it.calculateValues(remainingTime - 1, closedValue, openedValve, currentRouteName, this)
        }
        downStream
    }
}

data class Valve(
    val name: String,
    val flowRate: Int,
    val connections: MutableList<Valve> = mutableListOf()
) {
    override fun toString(): String {
        return "Valve(name='$name', flowRate=$flowRate, connections=${connections.map { it.name }})"
    }
}
