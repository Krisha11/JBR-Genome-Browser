package bed

import java.nio.file.Path
import java.io.RandomAccessFile

class FileWorker(path : Path, mode : String = "rw") {

    private val file : RandomAccessFile = RandomAccessFile(path.toFile(), mode)

    /**
     * Byte-by-byte read and add characters to the string
     * Returns pair of last byte and string
    */
    private fun readString() : Pair<Int, String> {
        val cur = StringBuilder()
        var byte = file.read()
        while((byte != -1) && (byte.toChar() != '\n')) {
            cur.append(byte.toChar())
            byte = file.read()
        }
        return byte to cur.toString()
    }

    /**
     * Reads and returns a list of pairs from the lines of the
     * source file and the numbers of their first bytes
     */
    fun read() : List<Pair<String, Long>> {

        val res : MutableList<Pair<String, Long>> = mutableListOf<Pair<String, Long>>()
        var byte = 0

        while (byte != -1) {
            val curIndex : Long = file.filePointer

            // Pair < last byte, string from input >
            val readRes = readString()
            byte = readRes.first

            if (readRes.second.isNotEmpty())
                res.add(readRes.second to curIndex)
        }

        return res.toList()
    }

    /**
     * Reads a line from a file from the numberSymbol-th byte
     */
    fun readLineFrom(numberSymbol : Long) : String {
        file.seek(numberSymbol)
        return readString().second
    }

    /**
     * Writing index to file
     */
    fun write(list : List<IndexEntry>) {
        val res = list.map {
                s : IndexEntry -> s.chromosome + ' ' + s.start.toString() + ' ' + s.end.toString() + ' ' + s.index.toString() + '\n'
        }.fold(StringBuilder()) {
                total, next -> total.append(next)
        }.toString()

        file.write(res.toByteArray())
    }

    fun close() {
        file.close()
    }

}