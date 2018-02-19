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

class PropCalculation1<T : Any, A>(val a: Prop<A>, val lambda: (A) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.listeners.add(this)
    }

    override fun eval(): T = lambda(a.value)
}

class PropCalculation2<T : Any, A, B>(val a: Prop<A>, val b: Prop<B>, val lambda: (A, B) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.listeners.add(this)
        if (!b.isConstant()) b.listeners.add(this)
    }

    override fun eval(): T = lambda(a.value, b.value)
}
