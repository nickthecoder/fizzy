package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.model.Parent
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop

class FindShape(prop: Prop<Parent>)
    : SpecialMethod<Parent>(prop) {

    lateinit var name: String

    override fun prepare(arg: Prop<*>) {
        if (arg.isConstant() && arg.value is String) {
            name = arg.value as String
        }

        throw RuntimeException("Expected arguments (String constant), but found $arg")
    }

    override fun evaluate(): Any {
        val shape = prop.value.findShape(name) ?: throw RuntimeException("Shape $name not found")
        if (shape !== calculatedValue) {
            calculatedValue?.let { unlistenTo((it as Shape).name) }
            listenTo(shape.name)
        }
        return shape
    }
}
