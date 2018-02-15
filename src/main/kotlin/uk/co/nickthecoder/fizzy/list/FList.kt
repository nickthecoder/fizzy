package uk.co.nickthecoder.fizzy.list

open class FList<T>(override val backing: List<T> = listOf<T>())

    : FCollection<T>, List<T> {

    override val listeners = CollectionListeners<T>()

    override val size
        get() = backing.size

    override fun contains(element: T) = super.contains(element)

    override fun iterator() = super.iterator()

    override fun containsAll(elements: Collection<T>) = super.containsAll(elements)

    override fun isEmpty() = super.isEmpty()

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

    : FList<T>(backing), MutableFCollection<T>, MutableList<T> {

    override fun add(index: Int, element: T) {
        backing.add(index, element)
        listeners.forEach { it.added(this, element) }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        var i = index
        var added = false
        elements.forEach { ele ->
            backing.add(i, ele)
            added = true
            listeners.forEach { it.added(this, ele) }
            i++
        }
        return added
    }

    override fun removeAt(index: Int): T {
        val result = backing.removeAt(index)
        if (result != null) {
            listeners.forEach { it.removed(this, result) }
        }
        return result
    }

    override fun set(index: Int, element: T): T {
        val result = backing.set(index, element)
        listeners.forEach { it.removed(this, result) }
        listeners.forEach { it.added(this, element) }

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
        return super.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return super.addAll(elements)
    }

    override fun remove(element: T): Boolean {
        return super.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return super.removeAll(elements)
    }

    override fun clear() {
        super.clear()
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return super.retainAll(elements)
    }

}
