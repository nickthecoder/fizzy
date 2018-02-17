package uk.co.nickthecoder.fizzy.prop

abstract class PropCalculation<T>(initialValue: T)

    : Prop<T>(initialValue) {

    protected var dirty: Boolean = true
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    listeners.forEach { it.dirty(this) }
                }
            }
        }

    protected var calculatedValue: T = initialValue

    override val value: T
        get() : T {
            if (dirty) {
                eval()
                dirty = false
            }
            return calculatedValue
        }

    abstract fun eval()
}

abstract class UnaryPropCalculation<T>(val a: Prop<T>, initialValue: T)

    : PropCalculation<T>(initialValue), PropListener<T> {

    init {
        a.listeners.add(this)
    }

    override fun dirty(prop: Prop<T>) {
        dirty = true
        listeners.forEach { it.dirty(this) }
    }

    override fun dump(): String {
        return "${this.javaClass.simpleName}( ${a.dump()} )"
    }
}


abstract class BinaryPropCalculation<T>(val a: Prop<T>, val b: Prop<T>, initialValue: T)

    : PropCalculation<T>(initialValue), PropListener<T> {

    init {
        a.listeners.add(this)
        b.listeners.add(this)
    }

    override fun dirty(prop: Prop<T>) {
        dirty = true
        listeners.forEach { it.dirty(this) }
    }

    override fun dump(): String {
        return "( ${a.dump()} ${this.javaClass.simpleName} ${b.dump()} )"
    }
}

abstract class GenericBinaryPropCalculation<T, A, B>(val a: Prop<A>, val b: Prop<B>, initialValue: T)

    : PropCalculation<T>(initialValue) {

    init {
        a.listeners.add(object : PropListener<A> {
            override fun dirty(prop: Prop<A>) {
                changed()
            }
        })
        b.listeners.add(object : PropListener<B> {
            override fun dirty(prop: Prop<B>) {
                changed()
            }
        })
    }

    fun changed() {
        dirty = true
        listeners.forEach { it.dirty(this) }
    }

}
