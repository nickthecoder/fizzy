/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
        items.filter { it.get() === listener }.firstOrNull()?.let {
            items.remove(it)
        }
    }

    /**
     * List the listeners as a simple string - useful for debugging.
     */
    fun list(): String = items.map { it.get() }.filterNotNull().joinToString()

}
