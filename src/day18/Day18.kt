package day18

import readInput
import kotlin.math.abs



fun main() {
    data class Cube(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {
        fun getPosition(): List<Int> {
            return listOf(x, y, z)
        }
    }

    fun Cube.checkTouching(to: Cube): Boolean {
        return (this.x == to.x && this.y == to.y && (abs(this.z - to.z) == 1)) ||
            (this.x == to.x && this.z == to.z && (abs(this.y - to.y) == 1)) ||
            (this.y == to.y && this.z == to.z && (abs(this.x - to.x) == 1))
    }

    fun parse(input: List<String>): List<Cube> {
        return input.map {
            it.split(",").map { it.toInt() }.let { (x, y, z) ->
                Cube(x, y, z)
            }
        }
    }

    fun Cube.countTouching(cubes: List<Cube>): Int {
        var freeSides = 6
        cubes.forEach { cube2 ->
            if (this != cube2) {
                if (this.checkTouching(cube2)) {
                    freeSides--
                }
            }
        }
        return freeSides
    }

    fun countSides(cubes: List<Cube>): Int {
        val freeSidesPerCube = cubes.map { cube ->
            cube.countTouching(cubes)
        }
        return freeSidesPerCube.sum()
    }

    fun part1(input: List<String>): Int {
        val cubes = parse(input)
        return countSides(cubes)
    }

    fun Cube.getNeighbours(): MutableList<Cube> {
        val neighbours = mutableListOf<Cube>()
        this.copy(x = this.x - 1).let { neighbours.add(it) }
        this.copy(x = this.x + 1).let { neighbours.add(it) }
        this.copy(y = this.y - 1).let { neighbours.add(it) }
        this.copy(y = this.y + 1).let { neighbours.add(it) }
        this.copy(z = this.z - 1).let { neighbours.add(it) }
        this.copy(z = this.z + 1).let { neighbours.add(it) }
        return neighbours
    }

    fun maxDimension(cubes: List<Cube>): Int {
        return cubes.maxOfOrNull {
            maxOf(it.x, it.y, it.z)
        } ?: 0
    }

    fun countOutside(cubes: List<Cube>): Int {
        val maxDim = maxDimension(cubes) + 1
        val minDim = -1

        var faceCount = 0

        val visited = mutableListOf<Cube>()
        val queue = ArrayDeque(listOf(Cube(-1, -1, -1)))

        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()
            currentPos.getNeighbours().forEach { n ->
                if (n.getPosition().all { it in minDim..maxDim }
                    && currentPos !in visited) {
                    if (n in cubes) {
                        faceCount++
                    } else {
                        queue.add(n)
                    }
                }

            }
            visited.add(currentPos)
        }

        return faceCount
    }

    fun part2(input: List<String>): Int {
        val cubes = parse(input)
        return countOutside(cubes)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day18/Day18_test")
    val input = readInput("day18/Day18")

    check((part1(testInput)).also { println(it) } == 64)
    check((part1(input)).also { println(it) } == 3374)
    check(part2(testInput).also { println(it) } == 58)
    check(part2(input).also { println(it) } == 2010)
}
