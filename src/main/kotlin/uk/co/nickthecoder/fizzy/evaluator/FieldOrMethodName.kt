package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.AbstractProp
import uk.co.nickthecoder.fizzy.prop.Prop

class FieldOrMethodName(name: String) : AbstractProp<String>() {

    override val value = name

    override fun findField(name: String): Prop<*>? = null

}