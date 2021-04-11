package bed

import java.nio.file.Path

val WHITESPACE = """(\s)+""".toRegex()

data class BedEntry(val chromosome: String, val start: Int, val end: Int, val other: List<Any>)

/**
 * Information stored in the index file
 */
data class IndexEntry(val chromosome: String, val start: Int, val end: Int, val index: Long)

fun String.toBedEntry() : BedEntry? {
    val parts = this.split(WHITESPACE).filter { x: String -> x.isNotEmpty() }

    // processing header lines
    if (this[0] == '#'
        || parts[0] == "browser"
        || parts[0] == "track")
            return null
    return BedEntry(parts[0], parts[1].toInt(), parts[2].toInt(), parts.subList(3, parts.size))
}

fun String.toIndexEntry() : IndexEntry {
    val parts = this.split(WHITESPACE).filter { x: String -> x.isNotEmpty() }
    return IndexEntry(parts[0], parts[1].toInt(), parts[2].toInt(), parts[3].toLong())
}

// i is the position of the BedEntry int the BED file.
fun BedEntry.toIndexEntry(i : Long) : IndexEntry {
    return IndexEntry(this.chromosome, this.start, this.end, i)
}

interface BedIndex {
    /**
     * Finds all entries that satisfy the condition.
     * Returns their positions in BED file
     */
    fun getIndexes(chromosome: String, start: Int, end: Int) : List<Long>
}

interface BedReader {
    /**
     * Creates index for [bedPath] and saves it to [indexPath]
     */
    fun createIndex(bedPath: Path, indexPath: Path)

    /**
     * Loads [BedIndex] instance from file [indexPath]
     */
    fun loadIndex(indexPath: Path): BedIndex

    /**
     * Loads list of [BedEntry] from file [bedPath] using [index].
     * All the loaded entries should be located on the given [chromosome],
     * and be inside the range from [start] inclusive to [end] exclusive.
     * E.g. entry [1, 2) is inside [0, 2), but not inside [0, 1).
     */
    fun findWithIndex(
        index: BedIndex, bedPath: Path,
        chromosome: String, start: Int, end: Int
    ): List<BedEntry>
}
