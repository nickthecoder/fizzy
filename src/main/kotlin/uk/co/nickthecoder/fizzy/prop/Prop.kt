package uk.co.nickthecoder.fizzy.prop

/**
 * The base class for all properties. Properties which have a value which can be changed should subclass
 * [PropConstant], whereas properties who's value changes dynamically (such as PropCalculation) do NOT
 * subclass [PropConstant], and therefore the value is a val.
 *
 * T should be an immutable type, so that the property value cannot change without the property's listeners being
 * notified.
 */
interface Prop<T> {

    val value: T

    val listeners: PropListeners<T>

    fun dump(): String = value.toString()
}

abstract class AbstractProp<T> : Prop<T> {

    override val listeners = PropListeners<T>()

    override fun toString(): String = "Prop: $value"
}

fun throwExpectedType(type: String, found: Prop<*>): Prop<*> {
    throw RuntimeException("Expected a $type, but found ${found.value?.javaClass?.simpleName}")
}
