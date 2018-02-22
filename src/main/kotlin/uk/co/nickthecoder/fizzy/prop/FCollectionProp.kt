package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection


class FCollectionPropType private constructor()
    : PropType<FCollection<*>>(FCollection::class) {

    override fun findField(prop: Prop<FCollection<*>>, name: String): Prop<*>? {
        return null
    }

    override fun findMethod(prop: Prop<FCollection<*>>, name: String): PropMethod<FCollection<*>, *>? {
        return null
    }

}

class FCollectionProp<T>(override val value: FCollection<T>) :
        AbstractProp<FCollection<*>>(), CollectionListener<T> {

    init {
        value.listeners.add(this)
    }

    override fun added(collection: FCollection<T>, item: T) {
        listeners.fireDirty(this)
    }

    override fun removed(collection: FCollection<T>, item: T) {
        listeners.fireDirty(this)
    }
    // TODO This won't notify listeners when an item in the list CHANGES.
    // Is that a problem?
}
