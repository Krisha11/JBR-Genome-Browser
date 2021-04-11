package bed

import java.nio.file.Path

class MyBedReader : BedReader {

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

    override fun loadIndex(indexPath: Path): BedIndex {
        val fileReader = FileWorker(indexPath, "r")

        val indexEntry = fileReader.read().map {
                (b, _) -> b.toIndexEntry()
        }

        fileReader.close()
        return MyBedIndex(indexEntry)
    }

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
