private fun Array<Array<Int>>.checkLeft(column: Int, row: Int): Boolean {
    val selectedColumns = (0 until column).map {
        this[row][it]
    }

    return selectedColumns.any { it >= this[row][column] }.not()
}

private fun Array<Array<Int>>.checkRight(column: Int, row: Int): Boolean {
    val selectedColumns = (column + 1 until this.first().size).map {
        this[row][it]
    }
    return selectedColumns.any { it >= this[row][column] }.not()
}

private fun Array<Array<Int>>.checkDown(column: Int, row: Int): Boolean {
    val selectedColumns = (0 until row).map {
        this[it][column]
    }
    return selectedColumns.any { it >= this[row][column] }.not()
}

private fun Array<Array<Int>>.checkUp(column: Int, row: Int): Boolean {
    val selectedColumns = (row + 1 until this.first().size).map {
        this[it][column]
    }
    return selectedColumns.any { it >= this[row][column] }.not()
}

private fun Array<Array<Int>>.countLeft(column: Int, row: Int): Int {
    val selectedColumns = (0 until column).map {
        this[row][it]
    }
    var isSmaller = true
    return selectedColumns.reversed().takeWhile {
        val r = isSmaller
        isSmaller = (it < this[row][column]) and isSmaller
        r
    }.count()
}

private fun Array<Array<Int>>.countRight(column: Int, row: Int): Int {
    val selectedColumns = (column + 1 until this.first().size).map {
        this[row][it]
    }

    var isSmaller = true
    return selectedColumns.takeWhile {
        val r = isSmaller
        isSmaller = (it < this[row][column]) and isSmaller
        r
    }.count()
}

private fun Array<Array<Int>>.countUp(column: Int, row: Int): Int {
    val selectedColumns = (0 until row).map {
        this[it][column]
    }
    var isSmaller = true
    return selectedColumns.reversed().takeWhile {
        val r = isSmaller
        isSmaller = (it < this[row][column]) and isSmaller
        r
    }.count()
}

private fun Array<Array<Int>>.countDown(column: Int, row: Int): Int {
    val selectedColumns = (row + 1 until this.first().size).map {
        this[it][column]
    }
    var isSmaller = true
    return selectedColumns.takeWhile {
        val r = isSmaller
        isSmaller = (it < this[row][column]) and isSmaller
        r
    }.count()
}

fun main() {
    fun readMap(input: List<String>) = input.map { row ->
        row.chunked(1).map { it.toInt() }.toTypedArray()
    }.toTypedArray()

    fun part1(input: List<String>): Int {
        val map = readMap(input)
        return map.mapIndexed { row, r ->
            r.mapIndexed { column, _ ->
                map.checkLeft(column, row) || map.checkUp(column, row)
                    || map.checkRight(column, row) || map.checkDown(column, row)
            }
        }.flatten().count { it }
    }

    fun part2(input: List<String>): Int {
        val map = readMap(input)
        return map.mapIndexed { row, r ->
            r.mapIndexed { column, _ ->
                map.countLeft(column, row) * map.countUp(column, row) *
                    map.countRight(column, row) * map.countDown(column, row)
            }
        }.flatten().max()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    println(part2(testInput))
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
