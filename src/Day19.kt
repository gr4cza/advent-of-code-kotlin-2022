@file:Suppress("MagicNumber")

fun main() {
    fun parse(input: List<String>): List<BluePrint> =
        input.map { line ->
            val sentences = line.split("""[\.:]""".toRegex()).filter { it.isNotEmpty() }.map { it.trim() }
            val id = sentences[0].split(" ").last().toInt()
            val oreCostOre = sentences[1].split(" ")[4].toInt()
            val clayCostOre = sentences[2].split(" ")[4].toInt()
            val obsidianCostOre = sentences[3].split(" ")[4].toInt()
            val obsidianCostClay = sentences[3].split(" ")[7].toInt()
            val geodeCostOre = sentences[4].split(" ")[4].toInt()
            val geodeCostObsidian = sentences[4].split(" ")[7].toInt()
            BluePrint(
                id = id,
                robots = listOf(
                    Pair(Robot(GeodeType.ORE), listOf(Cost(GeodeType.ORE, oreCostOre))),
                    Pair(Robot(GeodeType.CLAY), listOf(Cost(GeodeType.ORE, clayCostOre))),
                    Pair(
                        Robot(GeodeType.OBSIDIAN),
                        listOf(Cost(GeodeType.ORE, obsidianCostOre), Cost(GeodeType.CLAY, obsidianCostClay))
                    ),
                    Pair(
                        Robot(GeodeType.GEODE), listOf(
                            Cost(GeodeType.ORE, geodeCostOre),
                            Cost(GeodeType.OBSIDIAN, geodeCostObsidian)
                        )
                    )
                ),
            )
        }

    fun part1(input: List<String>): Int {
        val bluePrints = parse(input)
        val minutes = 24
        val startingRobots = listOf(Robot(GeodeType.ORE))

        val allQualityLevels = bluePrints.map { bluePrint ->
            val qualityLevel = bluePrint.calculateQualityLevel(startingRobots, minutes)
            qualityLevel * bluePrint.id
        }
            .also { println(it) }.sum()
        return allQualityLevels
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")

    check((part1(testInput)).also { println(it) } == 33)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 1)
    println(part2(input))
}

data class BluePrint(
    val id: Int,
    val robots: List<Pair<Robot, List<Cost>>>
) {
    fun calculateQualityLevel(
        currentRobots: List<Robot>,
        minutes: Int,
        collectedGeodes: Map<GeodeType, Int> = startingCollection()
    ): Int {
        if (minutes <= 0) return collectedGeodes[GeodeType.GEODE] ?: 0

        val geodes = robots.sortedByDescending { it.first.type }.mapNotNull {
            val currentGeodes = collectedGeodes.toMutableMap()
            val newRobots = tryToBuyRobot(currentGeodes, it)
            if (newRobots.isNotEmpty()) {
                produceGeodes(currentRobots, currentGeodes)
                calculateQualityLevel(currentRobots + newRobots, minutes - 1, currentGeodes)
            } else {
                null
            }
        }
        val currentGeodes = collectedGeodes.toMutableMap()
        produceGeodes(currentRobots, currentGeodes)
        val geodeWithoutBuy = calculateQualityLevel(currentRobots, minutes - 1, currentGeodes)

        return (geodes + listOf(geodeWithoutBuy)).max()
    }

    private fun tryToBuyRobot(
        currentGeodes: MutableMap<GeodeType, Int>,
        robotCost: Pair<Robot, List<Cost>>
    ): List<Robot> {
        val costs = robotCost.second
        val value = costs.minOfOrNull {
            val currentCount = currentGeodes[it.type] ?: 0
            currentCount / it.cost
        } ?: 0
        return if (value > 0) {
            costs.forEach { cost ->
                currentGeodes[cost.type]?.let { currentGeodes[cost.type] = it - (cost.cost * value) }
            }
            (0 until value).map {
                robotCost.first
            }
        } else {
            emptyList()
        }
    }

    private fun produceGeodes(
        currentRobots: List<Robot>,
        collectedGeodes: MutableMap<GeodeType, Int>
    ) {
        currentRobots.forEach { robot ->
            collectedGeodes[robot.type]?.let { collectedGeodes[robot.type] = it + 1 }
        }
    }

    private fun startingCollection(): MutableMap<GeodeType, Int> =
        mutableMapOf(
            GeodeType.ORE to 0,
            GeodeType.CLAY to 0,
            GeodeType.OBSIDIAN to 0,
            GeodeType.GEODE to 0,
        )
}

data class Robot(
    val type: GeodeType,
)

data class Cost(
    val type: GeodeType,
    val cost: Int,
)

enum class GeodeType {
    ORE, CLAY, OBSIDIAN, GEODE
}
