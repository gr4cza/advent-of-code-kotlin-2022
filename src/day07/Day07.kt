package day07

import readInput

fun main() {
    val dirs = """(\$ cd [a-zA-Z/.]+)""".toRegex()
    val files = """(\d+ .*)""".toRegex()


    fun parseDirContent(input: List<String>): MutableMap<String, Long> {
        val dirContent = mutableMapOf<String, Long>()
        val currentDirs = mutableListOf<String>()
        input
            .forEach {
                if (dirs.matches(it)) {
                    val (_, _, dir) = it.split(" ")
                    if (dir == "..") {
                        currentDirs.removeLast()
                    } else {
                        currentDirs.add(currentDirs.joinToString("") + dir)
                    }
                } else if (files.matches(it)) {
                    val dirValue = it.split(" ").first().toLong()
                    currentDirs.forEach { currentDir ->
                        dirContent[currentDir] = (dirContent[currentDir] ?: 0) + (dirValue)
                    }
                }
            }
        return dirContent
    }

    fun part1(input: List<String>): Long {
        val dirContent = parseDirContent(input)

        return dirContent.map { (_, sizes) ->
            sizes
        }.filter {
            it <= 100_000
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val dirContent = parseDirContent(input)
        val rootDir = dirContent.map { (_, sizes) -> sizes }.max()
        return dirContent.map { (_, sizes) ->
            sizes
        }.filter {
            (70_000_000 - (rootDir - it)) >= 30_000_000
        }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day07/Day07_test")
    check(part1(testInput).toInt() == 95437)

    val input = readInput("day07/Day07")
    println(part1(input))
    println(part2(input))
}
