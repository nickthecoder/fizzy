package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropCalculation

/**
 * Evaluated a field value of a Prop, for example, given an AngleProp, you can create a Field for "degrees" and "radians".
 * These will return a DoubleProp, which will be dynamically calculated.
 */
class Field<T : Any, F : Any>(val prop: Prop<T>, val lambda: (Prop<T>) -> F)
    : PropCalculation<F>() {

    override fun toString(): String {
        return "Field : $value"
    }

    override fun eval(): F = lambda(prop)
}
