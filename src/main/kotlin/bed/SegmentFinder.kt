package bed

/**
 * Contains all sequences for a fixed chromosome (scaffold).
 * Performs a quick search by segment [start, end) among them.
 * Returns a list of indexes of entries in Bed file.
 */
class SegmentFinder(inputList: List<IndexEntry>) {
    // List < (begin, List<(end, index)>) >
    private val segments: List<Pair<Int, List<Pair<Int, Long>>>>

    init {
        segments = inputList.groupBy {
            it.start
        }.map {
            (beg, lEnds) -> beg to lEnds.map {
                it.end to it.index
            }.sortedBy {
                it.first
            }
        }.sortedBy {
            it.first
        }
    }

    private fun getIndex(x : Int, list: List<Pair<Int, Any>>) : Int {
        val res : Int = list.binarySearchBy(x) {
            it.first
        }
        if (res < 0)
            return -res - 1
        return res
    }

    fun get(start : Int, end: Int) : List<Long> {
        val res : MutableList<Long> = mutableListOf()

        for (i in getIndex(start, segments) until getIndex(end + 1, segments)) {
            // [b, e) in segments[i].second - have fixed start :
            // segments[i].first and lie in [start, end)
            val b : Int = getIndex(start, segments[i].second)
            val e : Int = getIndex(end + 1, segments[i].second)
            if (b < e)
                res.addAll(segments[i].second.subList(b, e).map{ x -> x.second })
        }
        return res
    }

}
