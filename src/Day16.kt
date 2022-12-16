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
            closedValves = valves,
            openedValves = emptyList(),
            currentRoute = "",
        )
    }

    fun part2(input: List<String>): Int {
        val valves = parse(input)
        var remainingTime = 26
        var currentValve = valves.first { it.name == "AA" }

        return (currentValve to currentValve).calculateValues(
            remainingTime = remainingTime,
            closedValves = valves,
            openedValves = emptyList(),
            currentRoute = "" to "",
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")

    check((part1(testInput)).also { println(it) } == 1651)
    checkedRoutes1.clear()
    check((part1(input)).also { println(it) } == 1873)
    check(part2(testInput).also { println(it) } == 1707)
    checkedRoutes.clear()
    println(part2(input))
}

val checkedRoutes1 = HashMap<State1, Int>()

private fun Valve.calculateValues(
    remainingTime: Int,
    closedValves: List<Valve>,
    openedValves: List<Valve>,
    currentRoute: String,
    previous: Valve? = null
): Int {
    val currentRouteName = currentRoute + this.name
    if (checkedRoutes1.contains(State1(this.name, openedValves.map { it.name }.toSet(), remainingTime))) {
        return checkedRoutes1.getValue(State1(this.name, openedValves.map { it.name }.toSet(), remainingTime))
    }

    if (remainingTime <= 0 || closedValves.none { it.flowRate > 0 }) return 0

    val possibleConnections = getPossibleConnections(previous)

    val i = if (this.checkOpenableValve(closedValves)) {
        val downStream1 = openValve(closedValves, openedValves, possibleConnections, remainingTime, currentRouteName)
        val downStream2 = step(possibleConnections, remainingTime, closedValves, openedValves, currentRouteName)
        maxOf(downStream1, downStream2)
    } else {
        step(possibleConnections, remainingTime, closedValves, openedValves, currentRouteName)
    }
    checkedRoutes1[State1(this.name, openedValves.map { it.name }.toSet(), remainingTime)] = i
    return i
}

private fun Valve.openValve(
    closedValves: List<Valve>,
    openedValves: List<Valve>,
    possibleConnections: List<Valve>,
    remainingTime: Int,
    currentRouteName: String
): Int {
    val value = this.flowRate * (remainingTime - 1)

    val (newClosedValves, newOpenedValves) = newValves(closedValves, openedValves)
    if (newClosedValves.none { it.flowRate > 0 }) return value
    val downStream1 = possibleConnections.maxOf {
        it.calculateValues(remainingTime - 2, newClosedValves, newOpenedValves, currentRouteName, this)
    }
    return downStream1 + value
}

private fun Valve.step(
    possibleConnections: List<Valve>,
    remainingTime: Int,
    closedValves: List<Valve>,
    openedValves: List<Valve>,
    currentRouteName: String
) = possibleConnections.maxOf {
    it.calculateValues(remainingTime - 1, closedValves, openedValves, currentRouteName, this)
}

private fun Valve.newValves(
    closedValves: List<Valve>,
    openedValves: List<Valve>
): Pair<MutableList<Valve>, MutableList<Valve>> {
    val newClosedValves = closedValves.toMutableList()
    newClosedValves.remove(this)
    val newOpenedValves = openedValves.toMutableList()
    newOpenedValves.add(this)
    return Pair(newClosedValves, newOpenedValves)
}

val checkedRoutes = HashMap<State, Int>()

var counter: Long = 0

private fun Pair<Valve, Valve>.calculateValues(
    remainingTime: Int,
    closedValves: List<Valve>,
    openedValves: List<Valve>,
    currentRoute: Pair<String, String>,
    previous: Pair<Valve, Valve>? = null
): Int {
    val (me, elephant) = this
    val currentRouteName = currentRoute.first + me.name to currentRoute.second + elephant.name

    val key = State(me.name, elephant.name, openedValves.map { it.name }.toSet(), remainingTime)
    val key2 = State(elephant.name, me.name, openedValves.map { it.name }.toSet(), remainingTime)
    if (checkedRoutes.contains(key)) {
        return checkedRoutes.getValue(key)
    }
    if (checkedRoutes.contains(key2)) {
        return checkedRoutes.getValue(key2)
    }

    if (me.name != "AA" && elephant.name != "AA") {
        val chunked1 = currentRouteName.first.chunked(2)
        val chunked2 = currentRouteName.second.chunked(2)
        if (chunked1.contains(elephant.name) || chunked2.contains(me.name) ) {
            return 0
        }
    }
    if (detectedCircle(currentRouteName)) {
        return 0
    }

    if (remainingTime <= 0 || closedValves.none { it.flowRate > 0 }) return 0

    val myPossibleConnections = me.getPossibleConnections(previous?.first)
    val elephantPossibleConnections = elephant.getPossibleConnections(previous?.second)

    val meCheckOpenableValve = me.checkOpenableValve(closedValves)
    val elCheckOpenableValve = elephant.checkOpenableValve(closedValves)
    val newSum = if (meCheckOpenableValve && elCheckOpenableValve) {
        // both open
        val bothOpen: Int = if (me == elephant) {
            val value = me.flowRate * (remainingTime - 1)
            if (closedValves.none { it.flowRate > 0 }) return value

            val (newClosedValves, newOpenedValves) = me.newValves(closedValves, openedValves)
            val calculateValues =
                this.calculateValues(remainingTime - 1, newClosedValves, newOpenedValves, currentRouteName, this)

            calculateValues + value
        } else {
            val valueMe = me.flowRate * (remainingTime - 1)
            val valueEl = elephant.flowRate * (remainingTime - 1)
            val (newClosedValves, newOpenedValves) = me.newValves(closedValves, openedValves)
            val (newClosedValvesEl, newOpenedValvesEl) = elephant.newValves(newClosedValves, newOpenedValves)
            val calculateValues =
                this.calculateValues(remainingTime - 1, newClosedValvesEl, newOpenedValvesEl, currentRouteName, this)
            valueMe + valueEl + calculateValues
        }

        // me open
        val value = me.flowRate * (remainingTime - 1)
        if (closedValves.none { it.flowRate > 0 }) return value

        val (newClosedValves, newOpenedValves) = me.newValves(closedValves, openedValves)
        val elephantStep = elephantPossibleConnections.maxOf {
            (me to it).calculateValues(remainingTime - 1, newClosedValves, newOpenedValves, currentRouteName, me to it)
        }
        val meOpen = elephantStep + value

        // elephant open
        val valueEl = elephant.flowRate * (remainingTime - 1)
        if (closedValves.none { it.flowRate > 0 }) return valueEl

        val (newClosedValvesEl, newOpenedValvesEl) = elephant.newValves(closedValves, openedValves)
        val meStep = myPossibleConnections.maxOf {
            (it to elephant).calculateValues(
                remainingTime - 1,
                newClosedValvesEl,
                newOpenedValvesEl,
                currentRouteName,
                it to elephant
            )
        }
        val elephantOpen = meStep + valueEl

        //none open
        val noOpen = myPossibleConnections.cartesianProduct(elephantPossibleConnections).maxOf {
            it.calculateValues(remainingTime - 1, closedValves, openedValves, currentRouteName, me to elephant)
        }
        maxOf(bothOpen, meOpen, elephantOpen, noOpen)
    } else if (meCheckOpenableValve) {
        // me open
        val value = me.flowRate * (remainingTime - 1)
        if (closedValves.none { it.flowRate > 0 }) return value

        val (newClosedValves, newOpenedValves) = me.newValves(closedValves, openedValves)
        val elephantStep = elephantPossibleConnections.maxOf {
            (me to it).calculateValues(remainingTime - 1, newClosedValves, newOpenedValves, currentRouteName, me to it)
        }
        val meOpen = elephantStep + value

        //none open
        val noOpen = myPossibleConnections.cartesianProduct(elephantPossibleConnections).maxOf {
            it.calculateValues(remainingTime - 1, closedValves, openedValves, currentRouteName, me to elephant)
        }
        maxOf(meOpen, noOpen)
    } else if (elCheckOpenableValve) {
        // elephant open
        val value = elephant.flowRate * (remainingTime - 1)
        if (closedValves.none { it.flowRate > 0 }) return value

        val (newClosedValves, newOpenedValves) = elephant.newValves(closedValves, openedValves)
        val meStep = myPossibleConnections.maxOf {
            (it to elephant).calculateValues(
                remainingTime - 1,
                newClosedValves,
                newOpenedValves,
                currentRouteName,
                it to elephant
            )
        }
        val elephantOpen = meStep + value

        //none open
        val noOpen = myPossibleConnections.cartesianProduct(elephantPossibleConnections).maxOf {
            it.calculateValues(remainingTime - 1, closedValves, openedValves, currentRouteName, me to elephant)
        }
        maxOf(elephantOpen, noOpen)
    } else {
        // both steps
        myPossibleConnections.cartesianProduct(elephantPossibleConnections).maxOf {
            it.calculateValues(remainingTime - 1, closedValves, openedValves, currentRouteName, me to elephant)
        }
    }

    checkedRoutes[key] = newSum

    counter++.also { if((counter % 10000) == 0L) println(it) }
    return newSum
}

fun detectedCircle(currentRouteName: Pair<String, String>): Boolean {
    return currentRouteName.first.chunked(4).windowed(2, 1).any { (f, s) -> f == s } ||
    currentRouteName.second.chunked(4).windowed(2, 1).any { (f, s) -> f == s }||
    currentRouteName.first.chunked(6).windowed(2, 1).any { (f, s) -> f == s } ||
    currentRouteName.second.chunked(6).windowed(2, 1).any { (f, s) -> f == s }||
        (currentRouteName.first == currentRouteName.second && currentRouteName.first != "AA")
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

data class State1(
    val me: String,
    val openedValves: Set<String>,
    val remainingSteps: Int,
)

data class State(
    val me: String,
    val elephant: String,
    val openedValves: Set<String>,
    val remainingSteps: Int,
)
