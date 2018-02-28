package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropMethod

abstract class SpecialMethod<T : Any>(prop: Prop<T>)
    : PropMethod<T>(prop) {

    private val firstTime = true

    override fun eval(arg: Prop<*>): Any {
        if (firstTime) {
            prepare(arg)
        }
        return evaluate()
    }

    abstract fun prepare(arg: Prop<*>)

    abstract fun evaluate() : Any
}
