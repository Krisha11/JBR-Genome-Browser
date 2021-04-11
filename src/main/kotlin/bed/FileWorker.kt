package bed

import java.nio.file.Path
import java.io.RandomAccessFile

class FileWorker(private val path : Path, private val mode : String = "rw") {

    private val file : RandomAccessFile = RandomAccessFile(path.toFile(), mode)


    // этот метод читает файл и выводит его содержимое
    fun read() : List<Pair<String, Long>> {

        val res : MutableList<Pair<String, Long>> = mutableListOf<Pair<String, Long>>()
        var b = 0

        // побайтово читаем символы и плюсуем их в строку
        while (b != -1) {
            val cur = StringBuilder()
            val curIndex : Long = file.filePointer

            b = file.read()
            while((b != -1) && (b.toChar() != '\n')) {
                cur.append(b.toChar())
                b = file.read()
            }

            if (cur.toString().isNotEmpty())
                res.add(cur.toString() to curIndex)
        }

        return res.toList()
    }

    // читаем строку из файла с определенного символа
    fun readLineFrom(numberSymbol : Long) : String {
        val res = StringBuilder()

        // ставим указатель на нужный вам символ
        file.seek(numberSymbol)
        var b : Int = file.read()

        // побитово читаем и добавляем символы в строку
        while(b != -1 && b.toChar() != '\n') {
            res.append(b.toChar())
            b = file.read()
        }

        return res.toString()
    }

    // запись в файл с начала
    fun write(l : List<IndexEntry>) {
        val st = l.map {
                s : IndexEntry -> s.chromosome + ' ' + s.start.toString() + ' ' + s.end.toString() + ' ' + s.index.toString() + '\n'
        }.fold(StringBuilder()) {
                total, next -> total.append(next)
        }.toString()

        file.write(st.toByteArray())
    }

    fun close() {
        file.close()
    }

}