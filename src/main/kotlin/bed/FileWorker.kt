package bed

import java.nio.file.Path
import java.io.RandomAccessFile

class FileWorker(path : Path, mode : String = "rw") {

    private val file : RandomAccessFile = RandomAccessFile(path.toFile(), mode)

    // побайтово читаем и добавляем символы в строку
    private fun readString() : Pair<Int, String> {
        val cur = StringBuilder()
        var byte = file.read()
        while((byte != -1) && (byte.toChar() != '\n')) {
            cur.append(byte.toChar())
            byte = file.read()
        }
        return byte to cur.toString()
    }

    // этот метод читает файл и выводит его содержимое
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

    // читаем строку из файла с определенного символа
    fun readLineFrom(numberSymbol : Long) : String {
        file.seek(numberSymbol)
        return readString().second
    }

    // запись в файл с начала
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