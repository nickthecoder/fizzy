package uk.co.nickthecoder.fizzy.list

interface CollectionListener<T> {

    fun added(list: FCollection<T>, item: T)

    fun removed(list: FCollection<T>, item: T)

}
