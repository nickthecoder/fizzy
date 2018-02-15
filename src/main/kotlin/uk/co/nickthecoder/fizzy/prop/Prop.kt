package uk.co.nickthecoder.fizzy.prop

/**
 * T should be an immutable type, so that the property value cannot change without the property's listeners being
 * notified.
 */
abstract class Prop<T>(value: T) {

    val listeners = PropListeners<T>()

    open var value: T = value
        set(v) {
            if (v != field) {
                field = v
                listeners.forEach { it.changed(this) }
            }
        }

    open fun dump(): String = value.toString()
}
