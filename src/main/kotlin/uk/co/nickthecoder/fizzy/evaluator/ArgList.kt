package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.AbstractProp
import uk.co.nickthecoder.fizzy.prop.Prop

class ArgList : AbstractProp<MutableList<Prop<*>>>() {

    override val value = mutableListOf<Prop<*>>()

    override fun toString(): String {
        return "ArgList: $value"
    }

}
