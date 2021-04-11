
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import bed.*

import java.nio.file.Paths

const val testIndexFileName = "src/test/resources/testIndexFile"
const val testBedFileName = "src/test/resources/testBedFile.bed"
const val tst = "src/test/resources/tst"
const val testIndex = "src/test/resources/index"

val bedEntryStrings : List<String> = listOf("chr7    127471196    127472363    Pos1    0    +    127471196    127472363    255,0,0",
    "chr7    127472363    127473530    Pos2    0    +    127472363    127473530    255,0,0",
    "chr7    127473530    127474697    Pos3    0    +    127473530    127474697    255,0,0",
    "chr7    127474697    127475864    Pos4    0    +    127474697    127475864    255,0,0",
    "chr7    127475864    127477031    Neg1    0    -    127475864    127477031    0,0,255",
    "chr7    127477031    127478198    Neg2    0    -    127477031    127478198    0,0,255",
    "chr7    127478198    127479365    Neg3    0    -    127478198    127479365    0,0,255",
    "chr7    127479365    127480532    Pos5    0    +    127479365    127480532    255,0,0",
    "chr7    127480532    127481699    Neg4    0    -    127480532    127481699    0,0,255")

val bedIndexStrings = listOf("chr7 127471196 127472363 147",
    "chr7 127472363 127473530 233",
    "chr7 127473530 127474697 319",
    "chr7 127474697 127475864 405",
    "chr7 127475864 127477031 491",
    "chr7 127477031 127478198 577",
    "chr7 127478198 127479365 663",
    "chr7 127479365 127480532 749",
    "chr7 127480532 127481699 835")

val bedEntries = listOf(BedEntry("chr7", 127471196, 127472363, listOf("Pos1", "0", "+", "127471196", "127472363", "255,0,0")),
    BedEntry("chr7", 127472363, 127473530, listOf("Pos2", "0", "+", "127472363", "127473530", "255,0,0")),
    BedEntry("chr7", 127473530, 127474697, listOf("Pos3", "0", "+", "127473530", "127474697", "255,0,0")),
    BedEntry("chr7", 127474697, 127475864, listOf("Pos4", "0", "+", "127474697", "127475864", "255,0,0")),
    BedEntry("chr7", 127475864, 127477031, listOf("Neg1", "0", "-", "127475864", "127477031", "0,0,255")),
    BedEntry("chr7", 127477031, 127478198, listOf("Neg2", "0", "-", "127477031", "127478198", "0,0,255")),
    BedEntry("chr7", 127478198, 127479365, listOf("Neg3", "0", "-", "127478198", "127479365", "0,0,255")),
    BedEntry("chr7", 127479365, 127480532, listOf("Pos5", "0", "+", "127479365", "127480532", "255,0,0")),
    BedEntry("chr7", 127480532, 127481699, listOf("Neg4", "0", "-", "127480532", "127481699", "0,0,255")))

val indexEntries = listOf(IndexEntry("chr7", 127471196, 127472363, 147),
    IndexEntry("chr7", 127472363, 127473530, 233),
    IndexEntry("chr7", 127473530, 127474697, 319),
    IndexEntry("chr7", 127474697, 127475864, 405),
    IndexEntry("chr7", 127475864, 127477031, 491),
    IndexEntry("chr7", 127477031, 127478198, 577),
    IndexEntry("chr7", 127478198, 127479365, 663),
    IndexEntry("chr7", 127479365, 127480532, 749),
    IndexEntry("chr7", 127480532, 127481699, 835))


class TestTrie {
    @Test
    fun test() {
        val t1 = MyBedIndex(emptyList())
        assertTrue(t1.getIndexes("chr7", 0, 1000000000).isEmpty())

        val t2 = MyBedIndex(listOf(indexEntries[0]))
        assertEquals(t2.getIndexes("chr7", 0, 1000000000), listOf(indexEntries[0].index))

        val t3 = MyBedIndex(indexEntries)
        assertEquals(t3.getIndexes("chr7", 0, 1000000000), indexEntries.map { it.index })
        assertTrue(t3.getIndexes("chr7", 0, 0).isEmpty())
        assertEquals(t3.getIndexes("chr7", 127471196, 127472363), listOf(indexEntries[0].index))
    }
}


class TestFileWorker {

    @Test
    fun testReadBed() {
        val fileReader = FileWorker(Paths.get(testBedFileName), "r")
        val res = fileReader.read().map {
            (a, b) -> a.toBedEntry() to b
        }.filter {
            (a, _) -> a != null
        }
        assertEquals(res, bedEntries.zip(indexEntries).map {
                (a, b) -> a to b.index
        })
        fileReader.close()
    }

    @Test
    fun testReadIndex() {
        val fileReader = FileWorker(Paths.get(testIndexFileName), "r")
        val res = fileReader.read()
        
        assertEquals(res.map {
            it.first
        }, bedIndexStrings)
        fileReader.close()
    }

    @Test
    fun testWriteIndex() {
        val fileWriter = FileWorker(Paths.get(tst), "rw")
        fileWriter.write(indexEntries)
        fileWriter.close()

        val fileReaderTst = FileWorker(Paths.get(testIndexFileName), "r")
        val fileReaderCorrect = FileWorker(Paths.get(testIndexFileName), "r")
        val resTst = fileReaderTst.read()
        val resCorrect = fileReaderCorrect.read()
        assertEquals(resTst, resCorrect)
        fileReaderTst.close()
        fileReaderCorrect.close()
    }

    @Test
    fun testReadOneBed() {
        val fileReader = FileWorker(Paths.get(testBedFileName), "r")
        for (i in 0 until indexEntries.size) {
            val res = fileReader.readLineFrom(indexEntries[i].index)
            if (i >= 3)
                assertEquals(res, bedEntryStrings[i])
        }
        fileReader.close()
    }
}



class TestEntriesClasses {

    @Test
    fun test() {
        for (i in bedEntryStrings.indices) {
            assertEquals(bedEntryStrings[i].toBedEntry(), bedEntries[i])
            assertEquals(bedIndexStrings[i].toIndexEntry(), indexEntries[i])
            assertEquals(bedEntries[i].toIndexEntry(indexEntries[i].index), indexEntries[i])
        }
    }
}


class TestBedReader {

    @Test
    fun testCreateIndex() {
        val bedReader = MyBedReader()
        bedReader.createIndex(Paths.get(testBedFileName), Paths.get(testIndex))

        val fileReader = FileWorker(Paths.get(testIndex), "r")
        assertEquals(fileReader.read().map {
            it.first
        }, bedIndexStrings)
        fileReader.close()
    }

    @Test
    fun testFindWithIndex() {
        val bedReader = MyBedReader()
        val index = MyBedIndex(indexEntries)
        val path = Paths.get(testBedFileName)

        assertEquals(bedReader.findWithIndex(index, path, "chr7", 127471196, 127472363), listOf(bedEntries[0]))
        assertEquals(bedReader.findWithIndex(index, path, "chr7", 0, 1000000000), bedEntries)
        assertTrue(bedReader.findWithIndex(index, path, "chr8", 0, 1000000000).isEmpty())
    }

}


class TestBedIndex {

    @Test
    fun test() {
        val index = MyBedIndex(indexEntries)
        assertEquals(index.getIndexes("chr7", 127471196, 127472363), listOf(indexEntries[0].index))

        assertEquals(index.getIndexes("chr7", 0, 1000000000), indexEntries.map {
            it.index
        })
        assertTrue(index.getIndexes("chr8", 0, 1000000000).isEmpty())
    }
}

