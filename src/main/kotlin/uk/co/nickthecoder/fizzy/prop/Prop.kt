package uk.co.nickthecoder.fizzy.prop

/**
 * The base class for all properties. Properties which have a value which can be changed should subclass
 * [PropValue], whereas properties who's value changes dynamically (such as PropCalculation) do NOT
 * subclass [PropValue], and therefore the value is a val.
 *
 * T should be an immutable type, so that the property value cannot change without the property's listeners being
 * notified.
 */
open class Prop<T>(open val value: T) {

    val listeners = PropListeners<T>()

    override fun toString(): String = "Prop : $value"

    open fun dump(): String = value.toString()
}

/**
 * A property whose value can be changed (i.e. it is a var).
 */
open class PropValue<T>(value: T) : Prop<T>(value) {

    override var value: T = value
        set(v) {
            if (v != field) {
                field = v
                listeners.forEach { it.dirty(this) }
            }
        }
}

fun conversionExpected(type: String, found: Prop<*>): Prop<*> {
    throw RuntimeException("Expected a $type, but found ${found.value?.javaClass?.simpleName}")
}
