package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.AbstractProp

class FieldOrMethodName(name: String) : AbstractProp<String>() {

    override val value = name

    override fun toString(): String {
        return "FieldOrMethodName : $value"
    }
}
