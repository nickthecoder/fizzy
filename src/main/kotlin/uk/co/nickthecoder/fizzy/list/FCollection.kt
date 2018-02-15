package uk.co.nickthecoder.fizzy.list

interface FCollection<T>
    : Collection<T> {

    val listeners : CollectionListeners<T>

    val backing: Collection<T>

    override val size: Int
        get() = backing.size

    override fun contains(element: T) = backing.contains(element)

    override fun containsAll(elements: Collection<T>) = backing.containsAll(elements)

    override fun isEmpty() = backing.isEmpty()

    override fun iterator() = backing.iterator()
}

interface MutableFCollection<T>

    : FCollection<T>, MutableCollection<T> {

    override val backing: MutableCollection<T>

    override fun iterator() = backing.iterator()

    override fun add(element: T): Boolean {
        if (backing.add(element)) {
            listeners.forEach { it.added(this, element) }
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        elements.forEach { ele ->
            if (backing.add(ele)) {
                added = true
                listeners.forEach { it.added(this, ele) }
            }
        }
        return added
    }

    override fun clear() {
        backing.forEach { item ->
            listeners.forEach { it.removed(this, item) }
        }
        backing.clear()
    }

    override fun remove(element: T): Boolean {
        if (backing.remove(element)) {
            listeners.forEach { it.removed(this, element) }
            return true
        }
        return false
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
        val oldList = backing.toList()
        val result = backing.retainAll(elements)
        oldList.forEach { old ->
            if (!backing.contains(old)) {
                listeners.forEach { it.removed(this, old) }
            }
        }
        return result
    }

}

