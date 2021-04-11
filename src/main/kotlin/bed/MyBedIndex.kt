package bed


class MyBedIndex(l: List<IndexEntry>) : BedIndex {
//    private val trie : Trie = Trie()

    private val m: HashMap<String, SegmentFinder> = hashMapOf()

    init {
        l.groupBy {
            it.chromosome
        }.forEach {
            (s, l) -> m.put(s, SegmentFinder(l))
        }
    }
/*
   constructor(m_: HashMap<String, List<IndexEntry>>) : this() {
        m = m_.foreach { (i, l) ->
            i to SegmentFinder(l)
        }
    }
 */
/*
    init {
        l.groupBy {
            it.chromosome
        }.forEach {
                (x, l) -> trie.add(l, x)
        }
    }
*/

    override fun getIndexes(chromosome: String, start: Int, end: Int) : List<Long> {
        return m[chromosome]?.get(start, end) ?: emptyList()
/*        return trie.get(chromosome, start, end).map {
            it.index
        }
 */   }
}
