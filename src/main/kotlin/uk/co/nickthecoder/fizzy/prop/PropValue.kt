package uk.co.nickthecoder.fizzy.prop

open class PropValue<T : Any>(override val value: T)
    : Prop<T> {

    override val propListeners = PropListeners()

}
