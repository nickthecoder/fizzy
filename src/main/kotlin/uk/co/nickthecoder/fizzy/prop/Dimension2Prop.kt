package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2

class Dimension2PropType private constructor()
    : PropType<Dimension2>(Dimension2::class) {

    override fun findField(prop: Prop<Dimension2>, name: String): PropField<Dimension2, *>? {

        return when (name) {
            "x" -> PropField<Dimension2, Dimension>(prop) { prop.value.x }
            "y" -> PropField<Dimension2, Dimension>(prop) { prop.value.y }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<Dimension2>, name: String): PropMethod<Dimension2, *>? {
        return when (name) {
            "length" -> PropMethod0(prop) { prop.value.length() }
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "ratio" -> PropMethod1(prop, Dimension2::class) { prop.value.ratio(it) }
            "angle" -> PropMethod0(prop) { prop.value.angle() }
            "rotate" -> PropMethod1(prop, Angle::class) { prop.value.rotate(it) }
            else -> null
        }
    }

    companion object {
        val instance = Dimension2PropType()
    }
}

class Dimension2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Dimension2>(expression, Dimension2::class, context)
