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
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    val input = readInput("Day22")

    check((part1(testInput)).also { println(it) } == 6032)
    println(part1(input))
    check(part2(testInput).also { println(it) } == 5031)
    println(part2(input))
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
    val rotation: Rotation?
)

class Board(
    rows: List<String>
) {
    private val grid: List<List<Int>>
    private var currentPos: Position
    private var currentDir: Direction = Direction.R

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
                0 -> {
                    currentPos = newPosition
                }

                1 -> {
                    return@repeat
                }

                -1 -> {
                    when (currentDir) {
                        Direction.U -> {
                            if (moveDir(currentPos.copy(y = grid.size), Direction.U)) return@repeat
                        }

                        Direction.D -> {
                            if (moveDir(currentPos.copy(y = 0), Direction.D)) return@repeat
                        }

                        Direction.L -> {
                            if (moveDir(currentPos.copy(x = grid.first().size), Direction.L)) return@repeat
                        }

                        Direction.R -> {
                            if (moveDir(currentPos.copy(x = 0), Direction.R)) return@repeat
                        }
                    }
                }
            }
        }.also {
            currentDir =rotation?.let { currentDir.rotate(it) } ?: currentDir
        }
    }

    private fun moveDir(newP: Position, dir: Direction): Boolean {
        var newP1 = newP
        while (grid[newP1] == -1) {
            newP1 = newP1.newPosition(dir)
        }
        if (grid[newP1] == 1) return true
        if (grid[newP1] == 0) currentPos = newP1
        return false
    }

    fun evaluate(): Int {
        return 1000 * (currentPos.y + 1) + (4 * (currentPos.x + 1)) + when (currentDir) {
            Direction.R -> 0
            Direction.D -> 1
            Direction.L -> 2
            Direction.U -> 3
        }
    }

    private operator fun List<List<Int>>.get(pos: Position): Int {
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
