package bed


class MyBedIndex(indexEntries: List<IndexEntry>) : BedIndex {
    private val map: HashMap<String, SegmentFinder> = hashMapOf()

    init {
        indexEntries.groupBy {
            it.chromosome
        }.forEach {
            (s, l) -> map.put(s, SegmentFinder(l))
        }
    }

    override fun getIndexes(chromosome: String, start: Int, end: Int) : List<Long> {
        return map[chromosome]?.get(start, end) ?: emptyList()
    }
}
