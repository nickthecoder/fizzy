package uk.co.nickthecoder.fizzy.prop

abstract class PropExpression<T>(initialValue: T)

    : Prop<T>(initialValue) {

    protected var dirty: Boolean = true

    override var value: T
        set(v) {
            super.value = v
        }
        get() : T {
            if (dirty) {
                eval()
                dirty = false
            }
            return super.value
        }

    abstract fun eval()
}

abstract class UnaryPropExpression<T>(val a: Prop<T>, initialValue: T)

    : PropExpression<T>(initialValue), PropListener<T> {

    init {
        a.listeners.add(this)
    }

    override fun changed(prop: Prop<T>) {
        dirty = true
        listeners.forEach { it.changed(this) }
    }

    override fun dump(): String {
        return "${this.javaClass.simpleName}( ${a.dump()} )"
    }
}


abstract class BinaryPropExpression<T>(val a: Prop<T>, val b: Prop<T>, initialValue: T)

    : PropExpression<T>(initialValue), PropListener<T> {

    init {
        a.listeners.add(this)
        b.listeners.add(this)
    }

    override fun changed(prop: Prop<T>) {
        dirty = true
        listeners.forEach { it.changed(this) }
    }

    override fun dump(): String {
        return "( ${a.dump()} ${this.javaClass.simpleName} ${b.dump()} )"
    }
}

abstract class GenericBinaryPropExpression<T, A, B>(val a: Prop<A>, val b: Prop<B>, initialValue: T)

    : PropExpression<T>(initialValue) {

    init {
        a.listeners.add(object : PropListener<A> {
            override fun changed(prop: Prop<A>) {
                changed()
            }
        })
        b.listeners.add(object : PropListener<B> {
            override fun changed(prop: Prop<B>) {
                changed()
            }
        })
    }

    fun changed() {
        dirty = true
        listeners.forEach { it.changed(this) }
    }

}
