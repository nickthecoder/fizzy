package uk.co.nickthecoder.fizzy.prop

/**
 * Dynamically evaluates a field value of a Prop, for example, a Prop<Angle>, ".degrees" and ".radians" will create a
 * [PropField] object of type Prop<Double>.
 * This is a dynamically calculated value. Therefore, if the underlying Prop<Angle> changes its
 * value, the the [PropField] will also update.
 */
class PropField<T : Any, F : Any>(val prop: Prop<T>, val lambda: (Prop<T>) -> F)
    : PropCalculation<F>() {

    init {
        prop.listeners.add(this)
    }

    override fun eval(): F = lambda(prop)

    override fun toString(): String {
        return "Field value=${safeValue()}"
    }
}
