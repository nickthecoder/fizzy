package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection

/**
 * Coordinates add/remove/change events from a collection of children (C) to their parent's listeners.
 *
 * When an item is added to the collection, [added] endures that the parent's [ChangeListeners] are notified
 * by firing a [ChangeListener.changed].
 * At the same time, the parent is added to the child's [ChangeListeners].
 *
 * When an item is removed from the collection, [removed] ensures that the parent's [ChangeListeners] are notified
 * by firing a [ChangeListener.changed].
 * At the same time, the parent is removed from the child's [ChangeListeners].
 *
 * When an item in the collection changes, the parent's [ChangeListener]s are notified by firing a
 * [ChangeListener.changed].
 */
open class ChangeAndCollectionListener<P : HasChangeListeners<P>, C : HasChangeListeners<C>>(
        val parent: P,
        child: FCollection<C>)

    : ChangeListener<C>, CollectionListener<C> {

    init {
        child.listeners.add(this)
    }

    override fun changed(item: C, changeType: ChangeType, obj: Any?) {
        parent.listeners.fireChanged(parent, changeType, obj)
    }

    override fun added(collection: FCollection<C>, item: C) {
        parent.listeners.fireChanged(parent, ChangeType.ADD, item)
        item.listeners.add(this)
    }

    override fun removed(collection: FCollection<C>, item: C) {
        parent.listeners.fireChanged(parent, ChangeType.REMOVE, item)
        item.listeners.remove(this)
    }

}
