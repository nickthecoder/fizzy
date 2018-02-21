package uk.co.nickthecoder.fizzy.collection

interface CollectionListener<T> {

    fun added(collection: FCollection<T>, item: T)

    fun removed(collection: FCollection<T>, item: T)

}
