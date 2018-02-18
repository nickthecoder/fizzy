package uk.co.nickthecoder.fizzy.prop

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class PropListeners<T> : Iterable<PropListener<T>> {

    val items = CopyOnWriteArrayList<WeakReference<PropListener<T>>>()

    override fun iterator(): Iterator<PropListener<T>> {
        // Remove garbage collected items from the list
        items.removeAll(items.filter { it.get() == null })

        return items.map { it.get() }.filterNotNull().iterator()
    }

    fun add(listener: PropListener<T>) {
        items.add(WeakReference(listener))
    }

    fun remove(listener: PropListener<T>) {
        items.filter { it.get() === listener }.first()?.let { items.remove(it) }
    }

    fun add(action: () -> Unit) {
        add(object : PropListener<T> {
            override fun dirty(prop: Prop<T>) {
                action()
            }
        })
    }

    fun list(): String = items.map { it.get() }.filterNotNull().joinToString()
}
