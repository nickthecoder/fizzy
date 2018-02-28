package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.evaluator.ArgList
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop

class JoinTo(prop: Prop<Shape>)
    : SpecialMethod<Shape>(prop) {

    private lateinit var otherShape: Shape
    private lateinit var pointProp: Prop<Dimension2>

    override fun prepare(arg: Prop<*>) {

        if (arg is ArgList && arg.value.size == 2) {

            val id = arg.value[0].value
            val arg2 = arg.value[1]

            if (id is Number && arg.value[0].isConstant() && arg2.value is Dimension2) {
                otherShape = prop.value.page().findShape(id.toInt()) ?: throw RuntimeException("Shape $id not found")
                @Suppress("UNCHECKED_CAST")
                pointProp = arg.value[1] as Prop<Dimension2>

                listenTo(pointProp)
                listenTo(otherShape.fromLocalToPage)
                listenTo(prop.value.parent.fromPageToLocal)
                return
            }
        }
        throw RuntimeException("Expected arguments (Number constant, Dimension2), but found $arg")
    }

    override fun evaluate(): Any {
        return prop.value.parent.fromPageToLocal.value * otherShape.fromLocalToPage.value * pointProp.value
    }
}
