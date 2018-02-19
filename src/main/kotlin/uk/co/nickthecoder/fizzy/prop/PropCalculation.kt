package uk.co.nickthecoder.fizzy.prop

abstract class PropCalculation<T : Any>

    : AbstractProp<T>(), PropListener {

    protected var dirty: Boolean = true
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    listeners.forEach { it.dirty(this) }
                }
            }
        }

    private lateinit var calculatedValue: T

    override fun dirty(prop: Prop<*>) {
        dirty = true
    }

    override val value: T
        get() : T {
            if (dirty) {
                calculatedValue = eval()
                dirty = false
            }
            return calculatedValue
        }

    abstract fun eval(): T

    /**
     * Returns the value, or null if the value could not be calculated.
     */
    fun safeValue(): T? {
        try {
            return value
        } catch (e: Exception) {
            return null
        }
    }

    override fun toString(): String = "${this.javaClass.simpleName}: ${safeValue()}"
}

abstract class UnaryPropCalculation<T : Any>(val a: Prop<T>)

    : PropCalculation<T>() {

    init {
        a.listeners.add(this)
    }

    override fun dirty(prop: Prop<*>) {
        dirty = true
        listeners.forEach { it.dirty(this) }
    }

    override fun dump(): String {
        return "${this.javaClass.simpleName}( ${a.dump()} )"
    }
}


abstract class BinaryPropCalculation<T : Any>(val a: Prop<T>, val b: Prop<T>)

    : PropCalculation<T>() {

    init {
        a.listeners.add(this)
        b.listeners.add(this)
    }

    override fun dump(): String {
        return "( ${a.dump()} ${this.javaClass.simpleName} ${b.dump()} )"
    }
}

abstract class GenericBinaryPropCalculation<T : Any, A, B>(val a: Prop<A>, val b: Prop<B>)

    : PropCalculation<T>() {

    init {
        a.listeners.add(this)
        b.listeners.add(this)
    }
}
