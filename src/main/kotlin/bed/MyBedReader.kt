package bed

import java.nio.file.Path

class MyBedReader : BedReader {

    /**
     * Creates index for [bedPath] and saves it to [indexPath]
     */
    override fun createIndex(bedPath: Path, indexPath: Path) {
        val fileReader = FileWorker(bedPath, "r")
        val fileWriter = FileWorker(indexPath, "rw")

        val indexEntry = fileReader.read().map {
                (b, i) -> b.toBedEntry()?.toIndexEntry(i)
        }.filterNotNull()

        fileWriter.write(indexEntry)

        fileReader.close()
        fileWriter.close()
    }

    /**
     * Loads [BedIndex] instance from file [indexPath]
     */
    override fun loadIndex(indexPath: Path): BedIndex {
        val fileReader = FileWorker(indexPath, "r")

        val indexEntry = fileReader.read().map {
                (b, _) -> b.toIndexEntry()
        }

        fileReader.close()
        return MyBedIndex(indexEntry)
    }

    /**
     * Loads list of [BedEntry] from file [bedPath] using [index].
     * All the loaded entries should be located on the given [chromosome],
     * and be inside the range from [start] inclusive to [end] exclusive.
     * E.g. entry [1, 2) is inside [0, 2), but not inside [0, 1).
     */
    override fun findWithIndex(
        index: BedIndex, bedPath: Path,
        chromosome: String, start: Int, end: Int
    ): List<BedEntry> {
        val fileReader = FileWorker(bedPath, "r")

        val res = index.getIndexes(chromosome, start, end).map {
                i -> fileReader.readLineFrom(i).toBedEntry()
        }.filterNotNull()

        fileReader.close()
        return res
    }
}
