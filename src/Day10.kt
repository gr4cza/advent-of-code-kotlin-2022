fun main() {
    val importantCycles = listOf(20, 60, 100, 140, 180, 220)


    fun checkImportantCycle(currentCycle: Int, registerX: Int): Int {
        if (currentCycle in importantCycles) {
            return registerX * currentCycle
        }
        return 0
    }

    fun part1(input: List<String>): Int {
        var registerX = 1
        var currentCycle = 1
        var cycleSum = 0
        input.forEach {
            if (it == "noop") {
                cycleSum += checkImportantCycle(currentCycle, registerX)
                currentCycle++
            } else {
                val (_, xChange) = it.split(" ")
                cycleSum += checkImportantCycle(currentCycle, registerX)
                currentCycle++
                cycleSum += checkImportantCycle(currentCycle, registerX)
                currentCycle++
                registerX += xChange.toInt()
            }
        }
        return cycleSum
    }

    fun drawAPixel(pixels: List<MutableList<Char>>, c: Char, pixelPos: Int) {
        pixels[pixelPos / 40][pixelPos % 40] = c
    }

    fun drawPixel(pixels: List<MutableList<Char>>, currentCycle: Int, registerX: Int) {
        val pixelPos = currentCycle - 1
        if (pixelPos % 40 in registerX - 1..registerX + 1) {
            drawAPixel(pixels, '#', pixelPos)
        } else {
            drawAPixel(pixels, '.', pixelPos)
        }
    }

    fun part2(input: List<String>) {
        var registerX = 1
        var currentCycle = 1
        val pixels = List(6) { MutableList(40) { ' ' } }
        input.forEach {
            if (it == "noop") {
                drawPixel(pixels, currentCycle, registerX)
                currentCycle++
            } else {
                val (_, xChange) = it.split(" ")
                drawPixel(pixels, currentCycle, registerX)
                currentCycle++
                drawPixel(pixels, currentCycle, registerX)
                currentCycle++
                registerX += xChange.toInt()
            }
        }
        pixels.forEach {
            println(it)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    println(part1(testInput))
    check(part1(testInput) == 13140)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
