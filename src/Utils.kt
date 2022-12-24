import java.io.File
import java.math.BigInteger
import java.nio.file.attribute.PosixFileAttributeView
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


data class Position(
    var x: Int,
    var y: Int,
){
    fun newPosition(dir: Direction): Position {
        return when (dir) {
            Direction.U -> Position(x = this.x, y = this.y - 1)
            Direction.D -> Position(x = this.x, y = this.y + 1)
            Direction.L -> Position(x = this.x - 1, y = this.y)
            Direction.R -> Position(x = this.x + 1, y = this.y)
        }
    }
}

enum class Direction {
    U, D, L, R,;

    companion object

}

data class Edges(
    val startPos: Position,
    val endPos: Position,
)

operator fun <E> List<E>.component6(): String {
    return this[5].toString()
}

fun <S, T> List<S>.cartesianProduct(other : List<T>) : List<Pair<S, T>> =
    this.flatMap { s ->
        List(other.size) { s }.zip(other)
    }

operator fun <E> List<List<E>>.get(pos: Position): E {
    return this[pos.y][pos.x]
}

operator fun <E> List<MutableList<E>>.set(pos: Position, element: E) {
    this[pos.y][pos.x] = element
}
