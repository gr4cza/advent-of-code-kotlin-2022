@file:Suppress("MagicNumber")

package day19

import readInput

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
                robots = mapOf(
                    GeodeType.ORE to listOf(Cost(GeodeType.ORE, oreCostOre)),
                    GeodeType.CLAY to listOf(Cost(GeodeType.ORE, clayCostOre)),
                    GeodeType.OBSIDIAN to
                        listOf(Cost(GeodeType.ORE, obsidianCostOre), Cost(GeodeType.CLAY, obsidianCostClay)),
                    GeodeType.GEODE to listOf(
                        Cost(GeodeType.ORE, geodeCostOre),
                        Cost(GeodeType.OBSIDIAN, geodeCostObsidian)

                    )
                ),
            )
        }

    fun part1(input: List<String>): Int {
        val bluePrints = parse(input)
        val minutes = 24
        val startingRobots = listOf(Robot(GeodeType.ORE))

        val allQualityLevels = bluePrints.mapIndexed { i, bluePrint ->
            println("check: $i")
            BluePrint.currentMax = 0
            val qualityLevel = bluePrint.calculateQualityLevel(startingRobots, minutes)
            qualityLevel * bluePrint.id
        }
            .also { println(it) }.sum()
        return allQualityLevels
    }

    fun part2(input: List<String>): Int {
        val bluePrints = parse(input)
        val minutes = 32
        val startingRobots = listOf(Robot(GeodeType.ORE))

        val allQualityLevels = bluePrints.take(3).mapIndexed { i, bluePrint ->
            println("check: $i")
            BluePrint.currentMax = 0
            val qualityLevel = bluePrint.calculateQualityLevel(startingRobots, minutes)
            qualityLevel
        }
            .also { println(it) }.reduce(Int::times)
        return allQualityLevels
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day19/Day19_test")
    val input = readInput("day19/Day19")

    check((part1(testInput)).also { println(it) } == 33)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 1)
    println(part2(input))
}

data class BluePrint(
    val id: Int,
    val robots: Map<GeodeType, List<Cost>>
) {

    private val maxTypes: Map<GeodeType, Int> = mapOf(
        GeodeType.ORE to (robots.values.flatten().filter { it.type == GeodeType.ORE }.maxOfOrNull { it.cost } ?: 0),
        GeodeType.CLAY to (robots.values.flatten().filter { it.type == GeodeType.CLAY }.maxOfOrNull { it.cost } ?: 0),
        GeodeType.OBSIDIAN to (robots.values.flatten().filter { it.type == GeodeType.OBSIDIAN }.maxOfOrNull { it.cost }
            ?: 0),
    )

    fun calculateQualityLevel(
        currentRobots: List<Robot>,
        minutes: Int,
        collectedGeodes: Map<GeodeType, Int> = startingCollection()
    ): Int {
        if (minutes <= 0 || !checkCanBeBetter(minutes, currentRobots, collectedGeodes)) {
            val geodeCount = collectedGeodes[GeodeType.GEODE] ?: 0
            if ((geodeCount > currentMax)) {
                currentMax = geodeCount
                println(currentMax)
            }
            return geodeCount
        }

        val geodeCount = mutableListOf<Int>()
        // buy robot
        if (checkType(collectedGeodes, GeodeType.GEODE)) {
            geodeCount.add(buyRobot(collectedGeodes, GeodeType.GEODE, currentRobots, minutes))
        } else {
            listOf(GeodeType.OBSIDIAN, GeodeType.CLAY, GeodeType.ORE).forEach { type ->
                if (checkType(collectedGeodes, type) && !checkMax(type, currentRobots)) {
                    geodeCount.add(buyRobot(collectedGeodes, type, currentRobots, minutes))
                }
            }
            // produce
            val currentGeodes = collectedGeodes.toMutableMap()
            produceGeodes(currentRobots, currentGeodes)

            geodeCount.add(calculateQualityLevel(currentRobots, minutes - 1, currentGeodes))
        }

        return geodeCount.max()
    }

    private fun checkCanBeBetter(
        minutes: Int,
        currentRobots: List<Robot>,
        collectedGeodes: Map<GeodeType, Int>
    ): Boolean {
        val geodeRobotCount = currentRobots.count { it.type == GeodeType.GEODE }
        val geodeCount = collectedGeodes[GeodeType.GEODE]!!
        val generatedCount = (0 until minutes).map {
            geodeRobotCount + it
        }.sum()
        return geodeCount + generatedCount > currentMax
    }

    private fun checkMax(type: GeodeType, currentRobots: List<Robot>): Boolean {
        return currentRobots.count { it.type == type } >= maxTypes[type]!!
    }

    private fun checkType(collectedGeodes: Map<GeodeType, Int>, type: GeodeType) =
        robots[type]!!.all {
            it.cost <= collectedGeodes[it.type]!!
        }

    private fun buyRobot(
        collectedGeodes: Map<GeodeType, Int>,
        geode: GeodeType,
        currentRobots: List<Robot>,
        minutes: Int
    ): Int {
        val currentGeodes = collectedGeodes.toMutableMap()
        buyRobot(geode, currentGeodes)
        produceGeodes(currentRobots, currentGeodes)
        return calculateQualityLevel(
            currentRobots + listOf(Robot(geode)),
            minutes - 1,
            currentGeodes
        )
    }

    private fun buyRobot(geode: GeodeType, collectedGeodes: MutableMap<GeodeType, Int>) {
        robots[geode]!!.forEach {
            collectedGeodes[it.type] = collectedGeodes[it.type]!! - it.cost
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

    companion object {
        var currentMax = 0
    }
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
