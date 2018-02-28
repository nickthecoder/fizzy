package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.evaluator.ArgList
import uk.co.nickthecoder.fizzy.model.Geometry
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop


class JoinAlong(prop: Prop<Shape>)
    : SpecialMethod<Shape>(prop) {

    lateinit var geometryProp: Prop<Geometry>

    lateinit var alongProp: Prop<Double>

    lateinit var otherShape: Shape

    @Suppress("UNCHECKED_CAST")
    override fun prepare(arg: Prop<*>) {

        if (arg is ArgList && arg.value.size == 2) {

            if (arg.value[0].value is Geometry && arg.value[1].value is Double) {
                geometryProp = arg.value[0] as Prop<Geometry>
                alongProp = arg.value[1] as Prop<Double>

                otherShape = geometryProp.value.shape ?: throw RuntimeException("Geometry isn't connected to a Shape")

                // TODO Put these back once the recursion problem has been found.
                //listenTo(prop.value.fromPageToLocal)
                //listenTo(otherShape.fromLocalToPage)
                //listenTo(alongProp)
                //listenTo(geometryProp)
                return
            }
        }

        throw RuntimeException("Expected arguments (Geometry, Double), but found $arg")
    }

    override fun evaluate(): Any {
        return prop.value.fromPageToLocal.value * (otherShape.fromLocalToPage.value * geometryProp.value.pointAlong(alongProp.value))
    }
}
