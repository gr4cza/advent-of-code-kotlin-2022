package day15

import Edges
import Position
import readInput
import kotlin.math.abs

fun main() {
    fun parse(input: List<String>): Pair<List<Position>, List<Position>> {
        return input.map { line ->
            val chunks = line.split(" ").map { it.replace(Regex("""[xy,:=]"""), "") }
            Position(chunks[2].toInt(), chunks[3].toInt()) to
                Position(chunks[8].toInt(), chunks[9].toInt())
        }.unzip()
    }

    fun findEdges(positions: List<Position>): Edges {
        val xValues = positions.map { line ->
            line.x
        }
        val yValues = positions.map { line ->
            line.y
        }
        return Edges(
            Position(xValues.min(), yValues.min()),
            Position(xValues.max(), yValues.max()),
        )
    }

    fun Position.distance(to: Position): Int {
        return abs(to.x - this.x) + abs(to.y - this.y)
    }

    fun countEmptySpaces(sensors: List<Position>, beacons: List<Position>, atY: Int): Int {
        val beaconDistances = sensors.zip(beacons).map { (sensor, beacon) ->
            sensor to sensor.distance(beacon)
        }
        val minX = beaconDistances.map { (sensor, dist) ->
            sensor.x - dist
        }.min()
        val maxX = beaconDistances.map { (sensor, dist) ->
            sensor.x + dist
        }.max()
        return (minX..maxX).map { x ->
            val currentPos = Position(x, atY)
            beaconDistances.map { (sensor, dist) ->
                currentPos.distance(sensor) <= dist && !beacons.contains(currentPos)
            }.any { it }

        }.count { it }
    }

    fun part1(input: List<String>, atY: Int): Int {
        val (sensors, beacons) = parse(input)
        return countEmptySpaces(sensors, beacons, atY)
    }

    fun inRange(currentPos: Position, min: Int, max: Int): Boolean {
        return currentPos.x in min..max && currentPos.y in min..max
    }

    fun findSensor(sensors: List<Position>, beacons: List<Position>, min: Int, max: Int): Position {
        val beaconDistances = sensors.zip(beacons).map { (sensor, beacon) ->
            sensor to sensor.distance(beacon)
        }

        beaconDistances.forEach { (sensor, dist) ->
            sensor.positionsAtDist(dist + 1).forEach { position ->
                if (inRange(position, min, max)) {
                    if (beaconDistances.map { (s, d) ->
                            position.distance(s) > d
                        }.all { it }) {
                        return position
                    }
                }
            }
        }
        error(" ")
    }

    fun part2(input: List<String>, min: Int, max: Int): Long {
        val (sensors, beacons) = parse(input)
        val sensor = findSensor(sensors, beacons, min, max)
        return (sensor.x * 4000000L) + sensor.y
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day15/Day15_test")
    val input = readInput("day15/Day15")

    check((part1(testInput, 10)).also { println(it) } == 26)
    println(part1(input, 2_000_000))
    check(part2(testInput, 0, 20).also { println(it) } == 56000011L)
    println(part2(input, 0, 4000000))
}

private fun Position.positionsAtDist(dist: Int): Set<Position> {
    return (-dist..dist).map { delta ->
        listOf(
            Position(this.x + delta, this.y + (dist - delta)),
            Position(this.x + delta, this.y - (dist - delta)),
        )
    }.flatten().toSet()
}
