@file:Suppress("MagicNumber")

fun main() {

    fun parseInstructions(instructions: String): List<Inst> {
        val alteredInstr = instructions.replace("""([RL])""".toRegex(), " $0 ")
        return alteredInstr.split(" ")
            .windowed(2, 2, partialWindows = true).map { move ->
                val (count, rotate) = move.first() to move.last()
                Inst(
                    move = count.toInt(),
                    rotation = Rotation.value(rotate)
                )
            }
    }

    fun parse(input: List<String>): Pair<Board, List<Inst>> {
        val divider = input.indexOfFirst { it.isEmpty() }
        val board = Board(input.subList(0, divider))
        val instructions = parseInstructions(input.last())

        return board to instructions
    }

    fun parseCube(input: List<String>): Pair<Board, List<Inst>> {
        val divider = input.indexOfFirst { it.isEmpty() }
        val cube = Cube(input.subList(0, divider))
        val instructions = parseInstructions(input.last())

        return cube to instructions
    }

    fun simulate(board: Board, insts: List<Inst>) {
        insts.forEach { inst ->
            board.move(inst)
        }
    }

    fun part1(input: List<String>): Int {
        val (board, insts) = parse(input)
        simulate(board, insts)
        return board.evaluate()
    }

    fun part2(input: List<String>): Int {
        val (cube, insts) = parseCube(input)
        simulate(cube, insts)
        return cube.evaluate()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    val input = readInput("Day22")

    check((part1(testInput)).also { println(it) } == 6032)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 5031)
    println(part2(input))
}

class Cube(rows: List<String>) : Board(rows) {

    private val sides: List<List<Int>>
    private val n: Int

    init {
        val height = rows.size
        val width = rows.maxOf { it.length }
        val tempSides = List(height) { MutableList(width) { 0 } }
        val tempN = rows.first().chunked(1).count { it.isNotBlank() }
        n = if (tempN < 10) tempN else tempN / 2
        rows.forEachIndexed { y, row ->
            row.chunked(1).forEachIndexed { x, tile ->
                when (tile) {
                    " " -> tempSides[y][x] = 0
                    else -> tempSides[y][x] = calculateSide(x, y, n)
                }
            }
        }
        sides = tempSides
    }

    private fun calculateSide(x: Int, y: Int, n: Int): Int {
        return if (n < 10) {
            testSides(y, n, x)
        } else {
            normalSides(y, n, x)
        }
    }

    private fun testSides(y: Int, n: Int, x: Int) = when (y) {
        in 0 until n -> 1
        in n until 2 * n -> {
            when (x) {
                in 0 until n -> 2
                in n until 2 * n -> 3
                else -> 4
            }
        }

        else -> {
            when (x) {
                in 2 * n until 3 * n -> 5
                else -> 6
            }
        }
    }

    private fun normalSides(y: Int, n: Int, x: Int) = when (y) {
        in 0 until n -> {
            when (x) {
                in n until 2 * n -> 1
                else -> 2
            }
        }

        in n until 2 * n -> 3

        in 2 * n until 3 * n -> {
            when (x) {
                in 0 until n -> 4
                else -> 5
            }
        }

        else -> 6
    }

    override fun moveToOtherSide() {
        if (n < 10) {
            testMoveToOther()
        } else {
            normalMoveToOther()
        }
    }

    private fun testMoveToOther() {
        val side = sides[currentPos]
        when (currentDir) {
            Direction.U -> {
                when (side) {
                    2 -> moveDir(currentPos.copy(x = 2 * n + reverse(currentPos.x, n), y = 0), Direction.D)
                    3 -> moveDir(currentPos.copy(x = 2 * n, y = currentPos.x % n), Direction.R)
                    1 -> moveDir(currentPos.copy(x = reverse(currentPos.x, n), y = n), Direction.D)
                    6 -> moveDir(currentPos.copy(x = (3 * n - 1), y = 2 * n + reverse(currentPos.x, n)), Direction.L)
                    else -> error("Wrong state")
                }
            }

            Direction.D -> {
                when (side) {
                    2 -> moveDir(currentPos.copy(x = 2 * n + reverse(currentPos.x, n), y = 3 * n - 1), Direction.U)
                    3 -> moveDir(currentPos.copy(x = 2 * n, y = 2 * n + reverse(currentPos.x, n)), Direction.R)
                    5 -> moveDir(currentPos.copy(x = reverse(currentPos.x, n), y = 2 * n - 1), Direction.U)
                    6 -> moveDir(currentPos.copy(x = 0, y = n + reverse(currentPos.x, n)), Direction.R)
                    else -> error("Wrong state")
                }
            }

            Direction.L -> {
                when (side) {
                    1 -> moveDir(currentPos.copy(x = n + (currentPos.y % n), y = n), Direction.D)
                    2 -> moveDir(currentPos.copy(x = 3 * n + reverse(currentPos.y, n), y = 3 * n - 1), Direction.U)
                    5 -> moveDir(currentPos.copy(x = n + reverse(currentPos.y, n), y = 2 * n - 1), Direction.U)
                    else -> error("Wrong state")
                }
            }

            Direction.R -> {
                when (side) {
                    1 -> moveDir(currentPos.copy(x = (4 * n - 1), y = 2 * n + reverse(currentPos.y, n)), Direction.L)
                    4 -> moveDir(currentPos.copy(x = 3 * n + reverse(currentPos.y, n), y = (2 * n)), Direction.D)
                    6 -> moveDir(currentPos.copy(x = (3 * n - 1), y = reverse(currentPos.y, n)), Direction.L)
                    else -> error("Wrong state")
                }
            }
        }
    }

    private fun normalMoveToOther() {
        val side = sides[currentPos]
        when (currentDir) {
            Direction.U -> {
                when (side) {
                    4 -> moveDir(currentPos.copy(x = n, y = n + (currentPos.x % n)), Direction.R)
                    1 -> moveDir(currentPos.copy(x = 0, y = 3 * n + (currentPos.x % n)), Direction.R)
                    2 -> moveDir(currentPos.copy(x = (currentPos.x % n), y = 4 * n - 1), Direction.U)
                    else -> error("Wrong state")
                }
            }

            Direction.D -> {
                when (side) {
                    6 -> moveDir(currentPos.copy(x = 2 * n + (currentPos.x % n), y = 0), Direction.D)
                    5 -> moveDir(currentPos.copy(x = n - 1, y = 3 * n + (currentPos.x % n)), Direction.L)
                    2 -> moveDir(currentPos.copy(x = 2 * n - 1, y = n + (currentPos.x % n)), Direction.L)
                    else -> error("Wrong state")
                }
            }

            Direction.L -> {
                when (side) {
                    1 -> moveDir(currentPos.copy(x = 0, y = 2 * n + reverse(currentPos.y, n)), Direction.R)
                    3 -> moveDir(currentPos.copy(x = currentPos.y % n, y = 2 * n), Direction.D)
                    4 -> moveDir(currentPos.copy(x = n, y = reverse(currentPos.y, n)), Direction.R)
                    6 -> moveDir(currentPos.copy(x = n + (currentPos.y % n), y = 0), Direction.D)
                    else -> error("Wrong state")
                }
            }

            Direction.R -> {
                when (side) {
                    2 -> moveDir(currentPos.copy(x = 2 * n - 1, y = 2 * n + reverse(currentPos.y, n)), Direction.L)
                    3 -> moveDir(currentPos.copy(x = 2 * n + (currentPos.y % n), y = n - 1), Direction.U)
                    5 -> moveDir(currentPos.copy(x = 3 * n - 1, y = reverse(currentPos.y, n)), Direction.L)
                    6 -> moveDir(currentPos.copy(x = n + (currentPos.y % n), y = 3 * n - 1), Direction.U)
                    else -> error("Wrong state")
                }
            }
        }
    }

    private fun reverse(x: Int, n: Int): Int {
        return (n - 1) - (x % n)
    }

    override fun moveDir(newP: Position, dir: Direction) {
        var newP1 = newP
        while (grid[newP1] == -1) {
            newP1 = newP1.newPosition(dir)
        }
        if (grid[newP1] == 1) return
        if (grid[newP1] == 0) {
            currentPos = newP1
            currentDir = dir
        }
    }
}

private fun Direction.rotate(rotation: Rotation): Direction {
    return when (rotation) {
        Rotation.R -> {
            when (this) {
                Direction.U -> Direction.R
                Direction.R -> Direction.D
                Direction.L -> Direction.U
                Direction.D -> Direction.L
            }
        }

        Rotation.L -> {
            when (this) {
                Direction.U -> Direction.L
                Direction.R -> Direction.U
                Direction.L -> Direction.D
                Direction.D -> Direction.R
            }
        }
    }
}

data class Inst(
    val move: Int,
    val rotation: Rotation?,
)

open class Board(
    rows: List<String>,
) {
    val grid: List<MutableList<Int>>
    var currentPos: Position
    var currentDir: Direction = Direction.R

    init {
        val height = rows.size
        val width = rows.maxOf { it.length }
        val g = List(height) { MutableList(width) { -1 } }
        rows.forEachIndexed { y, row ->
            row.chunked(1).forEachIndexed { x, tile ->
                when (tile) {
                    " " -> g[y][x] = -1
                    "." -> g[y][x] = 0
                    "#" -> g[y][x] = 1
                    else -> error("Wrong input")
                }
            }
        }
        grid = g
        currentPos = findStart()
    }

    private fun findStart(): Position = Position(this.grid.first().indexOfFirst { it == 0 }, 0)

    override fun toString(): String {
        var y = 0
        return grid.joinToString("\n") { line ->
            line.mapIndexed { x, char ->
                if (currentPos == Position(x, y)) {
                    "x"
                } else {
                    when (char) {
                        0 -> "."
                        1 -> "#"
                        -1 -> " "
                        8 -> "8"
                        else -> "x"
                    }
                }
            }.also { y++ }.joinToString("", prefix = "", postfix = "")
        }
    }

    fun move(inst: Inst) {
        val (move, rotation) = inst
        repeat(move) {
            val newPosition = currentPos.newPosition(currentDir)
            when (grid[newPosition]) {
                0 -> currentPos = newPosition
                1 -> return@repeat
                -1 -> {
                    moveToOtherSide()
                    return@repeat
                }
            }

        }.also {
            currentDir = rotation?.let { currentDir.rotate(it) } ?: currentDir
        }
    }

    internal open fun moveToOtherSide() {
        when (currentDir) {
            Direction.U -> moveDir(currentPos.copy(y = grid.size), Direction.U)
            Direction.D -> moveDir(currentPos.copy(y = 0), Direction.D)
            Direction.L -> moveDir(currentPos.copy(x = grid.first().size), Direction.L)
            Direction.R -> moveDir(currentPos.copy(x = 0), Direction.R)
        }
    }

    internal open fun moveDir(newP: Position, dir: Direction) {
        var newP1 = newP
        while (grid[newP1] == -1) {
            newP1 = newP1.newPosition(dir)
        }
        if (grid[newP1] == 1) return
        if (grid[newP1] == 0) currentPos = newP1
    }

    fun evaluate(): Int {
        return 1000 * (currentPos.y + 1) + (4 * (currentPos.x + 1)) + when (currentDir) {
            Direction.R -> 0
            Direction.D -> 1
            Direction.L -> 2
            Direction.U -> 3
        }
    }

    operator fun List<List<Int>>.get(pos: Position): Int {
        return try {
            this[pos.y][pos.x]
        } catch (e: IndexOutOfBoundsException) {
            return -1
        }
    }
}


enum class Rotation {
    R, L;

    companion object {
        fun value(r: String): Rotation? = when (r) {
            "R" -> R
            "L" -> L
            else -> null
        }
    }
}
