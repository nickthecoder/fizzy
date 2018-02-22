package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.FList


class FListPropType private constructor()
    : PropType<FList<out Any>>(FList::class) {

    override fun findField(prop: Prop<FList<out Any>>, name: String): Prop<*>? {

        return when (name) {
            "size" -> PropCalculation1(prop) { v -> v.size }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<FList<out Any>>, name: String): PropMethod<FList<out Any>, *>? {
        return null
    }

    companion object {
        val instance = FListPropType()
    }
}

class FListProp<T>(override val value: FList<T>) :
        AbstractProp<FList<*>>(), CollectionListener<T> {

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
