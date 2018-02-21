package uk.co.nickthecoder.fizzy.util

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Contains listeners (of type T) using WeakReferences in a CopyOnWriteArrayList.
 *
 * Being WeakReferences it is vital that client code keeps a reference to the listener while they are still
 * needed. The advantage of WeakReferences is that the listeners cannot be the cause of memory leaks.
 *
 * A CopyOnWriteArrayList was chosen, so that concurrent modification exceptions are never thrown.
 * At the price of an overhead whenever listeners are added, or removed. If you are adding/removing listeners
 * very frequently, then another solution is probably required.
 */
open class Listeners<T : Any> : Iterable<T> {

    val items = CopyOnWriteArrayList<WeakReference<T>>()

    override fun iterator(): Iterator<T> {
        // Remove garbage collected items from the list
        items.removeAll(items.filter { it.get() == null })

        return items.map { it.get() }.filterNotNull().iterator()
    }

    fun add(listener: T) {
        items.add(WeakReference(listener))
    }

    fun remove(listener: T) {
        items.filter { it.get() === listener }.first()?.let { items.remove(it) }
    }

    /**
     * List the listeners as a simple string - useful for debugging.
     */
    fun list(): String = items.map { it.get() }.filterNotNull().joinToString()

}
