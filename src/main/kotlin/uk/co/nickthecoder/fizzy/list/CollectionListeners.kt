package uk.co.nickthecoder.fizzy.list

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class CollectionListeners<T> : Iterable<CollectionListener<T>> {

    val items = CopyOnWriteArrayList<WeakReference<CollectionListener<T>>>()

    override fun iterator(): Iterator<CollectionListener<T>> {
        // Remove garbage collected items from the list
        items.removeAll(items.filter { it.get() == null })

        return items.map { it.get() }.filterNotNull().iterator()
    }

    fun add(listener: CollectionListener<T>) {
        items.add(WeakReference(listener))
    }

    fun remove(listener: CollectionListener<T>) {
        items.filter { it.get() === listener }.first()?.let { items.remove(it) }
    }

}
