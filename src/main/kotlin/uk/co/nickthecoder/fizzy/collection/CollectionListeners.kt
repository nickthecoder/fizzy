package uk.co.nickthecoder.fizzy.collection

import uk.co.nickthecoder.fizzy.util.Listeners

class CollectionListeners<T>
    : Listeners<CollectionListener<T>>() {

    fun fireAdded(collection: FCollection<T>, element: T) {
        forEach { it.added(collection, element) }
    }

    fun fireRemoved(collection: FCollection<T>, element: T) {
        forEach { it.removed(collection, element) }
    }
}
