package uk.co.nickthecoder.fizzy.prop

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class PropListeners<T> : Iterable<PropListener> {

    val items = CopyOnWriteArrayList<WeakReference<PropListener>>()

    override fun iterator(): Iterator<PropListener> {
        // Remove garbage collected items from the list
        items.removeAll(items.filter { it.get() == null })

        return items.map { it.get() }.filterNotNull().iterator()
    }

    fun add(listener: PropListener) {
        items.add(WeakReference(listener))
    }

    fun remove(listener: PropListener) {
        items.filter { it.get() === listener }.first()?.let { items.remove(it) }
    }

    fun add(action: () -> Unit) {
        add(object : PropListener {
            override fun dirty(prop: Prop<*>) {
                action()
            }
        })
    }

    fun list(): String = items.map { it.get() }.filterNotNull().joinToString()
}
