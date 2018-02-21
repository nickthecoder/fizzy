package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Vector2

class Vector2PropType : PropType<Vector2>(Vector2::class) {

    override fun findField(prop: Prop<Vector2>, name: String): PropField<Vector2, *>? {
        return when (name) {
            "x" -> PropField<Vector2, Double>(prop) { prop.value.x }
            "y" -> PropField<Vector2, Double>(prop) { prop.value.y }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<Vector2>, name: String): PropMethod<Vector2, *>? {
        return when (name) {
            "length" -> PropMethod0(prop) { prop.value.length() }
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "angle" -> PropMethod0(prop) { prop.value.angle() }
            "rotate" -> PropMethod1(prop, Angle::class) { prop.value.rotate(it) }
            else -> null
        }
    }
}

class Vector2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Vector2>(expression, Vector2::class, context)

class Vector2Constant(value: Vector2 = Vector2.ZERO)
    : PropConstant<Vector2>(value) {
}
