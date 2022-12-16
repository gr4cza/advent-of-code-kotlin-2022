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
        val remainingTime = 30
        val currentValve = valves.first { it.name == "AA" }

        val calculateValues = currentValve.calculateRoutes(
            remainingTime = remainingTime,
            closedValves = valves,
        ).sortedByDescending { it.value }
        return calculateValues.maxOf { it.value }
    }

    fun part2(input: List<String>): Int {
        val valves = parse(input)
        val remainingTime = 26
        val currentValve = valves.first { it.name == "AA" }

        val calculateValues = currentValve.calculateRoutes(
            remainingTime = remainingTime,
            closedValves = valves,
        ).sortedByDescending { it.value }
        val val2 = calculateValues.find {
            it.valves.intersect(calculateValues.first().valves.toSet()).isEmpty()
        }
        return calculateValues.maxOf { it.value } + (val2?.value?:0)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")

//    check((part1(testInput)).also { println(it) } == 1651)
    check((part1(input)).also { println(it) } == 1873)
//    check(part2(testInput).also { println(it) } == 1707)
    check((part2(input)).also { println(it) } == 2425)
}


private fun Valve.calculateRoutes(
    remainingTime: Int,
    closedValves: List<Valve>,
    previous: Valve? = null
): List<Route> {
    if (remainingTime <= 0 || closedValves.none { it.flowRate > 0 }) return listOf(Route(0))

    val possibleConnections = getPossibleConnections(previous)

    return if (this.checkOpenableValve(closedValves)) {
        val value = this.flowRate * (remainingTime - 1)
        val newClosedValves = newValves(closedValves)
        val subRoutes =
            this.calculateRoutes(remainingTime - 1, newClosedValves, this)
        subRoutes.map {
            Route(it.value + value, listOf(this.name) + it.valves)
        }
    } else {
        possibleConnections.map {
            it.calculateRoutes(remainingTime - 1, closedValves, this)
        }.flatten()
    }

}

private fun Valve.newValves(
    closedValves: List<Valve>,
): MutableList<Valve> {
    val newClosedValves = closedValves.toMutableList()
    newClosedValves.remove(this)
    return newClosedValves
}

private fun Valve.checkOpenableValve(closedValves: List<Valve>) = this.flowRate > 0 && closedValves.contains(this)

private fun Valve.getPossibleConnections(previous: Valve?): List<Valve> {
    return if (this.connections.size == 1) {
        this.connections
    } else {
        this.connections.filter { it != previous }
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



data class Route(
    val value: Int,
    val valves: List<String> = listOf(),
)
