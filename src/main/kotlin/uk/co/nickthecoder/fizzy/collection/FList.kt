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
package uk.co.nickthecoder.fizzy.collection

open class FList<T>(open val backing: List<T> = listOf<T>())

    : List<T> {

    val listeners = ListListeners<T>()

    override val size
        get() = backing.size

    override fun contains(element: T) = backing.contains(element)

    override fun iterator() = backing.iterator()

    override fun containsAll(elements: Collection<T>) = backing.containsAll(elements)

    override fun isEmpty() = backing.isEmpty()

    // List operations

    override fun get(index: Int) = backing[index]

    override fun lastIndexOf(element: T) = backing.lastIndexOf(element)

    override fun indexOf(element: T) = backing.indexOf(element)

    override fun listIterator() = backing.listIterator()

    override fun listIterator(index: Int) = backing.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        return backing.subList(fromIndex, toIndex)
    }
}

open class MutableFList<T>(override val backing: MutableList<T> = mutableListOf<T>())

    : FList<T>(backing), MutableList<T> {

    override fun add(index: Int, element: T) {
        backing.add(index, element)
        listeners.fireAdded(this, element, index)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        var i = index
        var added = false
        elements.forEach { ele ->
            backing.add(i, ele)
            added = true
            listeners.fireAdded(this, ele, i)
            i++
        }
        return added
    }

    override fun remove(element: T): Boolean {
        val index = backing.indexOf(element)
        if (index < 0) return false
        removeAt(index)
        return true
    }

    override fun removeAt(index: Int): T {
        val result = backing.removeAt(index)
        if (result != null) {
            listeners.fireRemoved(this, result, index)
        }
        return result
    }

    fun removeLast(): T = removeAt(size - 1)

    override fun set(index: Int, element: T): T {
        val result = backing.set(index, element)
        backing.removeAt(index)
        backing.add(index, element)
        return result
    }

    override fun iterator(): MutableIterator<T> {
        return backing.iterator()
    }

    override fun listIterator(): MutableListIterator<T> {
        return backing.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return backing.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return backing.subList(fromIndex, toIndex)
    }

    // I don't see why I have to override each of these methods, but the compiler is forcing me to do it.
    override fun add(element: T): Boolean {
        if (backing.add(element)) {
            listeners.fireAdded(this, element, backing.size - 1)
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        elements.forEach { ele ->
            if (backing.add(ele)) {
                added = true
                listeners.fireAdded(this, ele, backing.size - 1)
            }
        }
        return added
    }

    override fun clear() {
        backing.forEach { item ->
            listeners.fireRemoved(this, item, 0)
        }
        backing.clear()
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var removed = false
        elements.forEach { ele ->
            if (remove(ele)) {
                removed = true
            }
        }
        return removed
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        // This is a slow, but safe implementation, removing single items at a time, in order to ensure that
        // listeners don't get intermediate results.
        val toRemove = backing.filter { it !in elements }
        return removeAll(toRemove)
    }

    override fun toString(): String = backing.toString()
}
