package uk.co.nickthecoder.fizzy.model

interface MetaDataAware {

    fun metaData(): MetaData

    fun createRow(type: String?): Pair<MetaDataAware, MetaData> {
        throw IllegalStateException("${this.javaClass.simpleName} has no rows ${if (type == null) "" else type}")
    }

    fun getSection(sectionName: String): Pair<Any, MetaData> {
        throw IllegalStateException("${this.javaClass.simpleName} has no section $sectionName")
    }

}
