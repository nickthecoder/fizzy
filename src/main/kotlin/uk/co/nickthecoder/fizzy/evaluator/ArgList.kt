package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.Prop

class ArgList : Prop<MutableList<Prop<*>>>(mutableListOf<Prop<*>>()) {

    override fun toString(): String {
        return "ArgList : " + super.toString()
    }

}
