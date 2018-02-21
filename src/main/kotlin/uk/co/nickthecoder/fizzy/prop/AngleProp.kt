package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Angle

class AnglePropType private constructor()
    : PropType<Angle>(Angle::class) {

    override fun findField(prop: Prop<Angle>, name: String): PropField<Angle, *>? {
        return when (name) {
            "degrees" -> PropField<Angle, Double>(prop) { prop.value.degrees }
            "radians" -> PropField<Angle, Double>(prop) { prop.value.radians }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<Angle>, name: String): PropMethod<Angle, *>? {
        return when (name) {
            "sin" -> PropMethod0(prop) { Math.sin(prop.value.radians) }
            "cos" -> PropMethod0(prop) { Math.cos(prop.value.radians) }
            "tan" -> PropMethod0(prop) { Math.tan(prop.value.radians) }

            "sinh" -> PropMethod0(prop) { Math.sinh(prop.value.radians) }
            "cosh" -> PropMethod0(prop) { Math.cosh(prop.value.radians) }
            "tanh" -> PropMethod0(prop) { Math.tanh(prop.value.radians) }
            else -> null
        }
    }

    companion object {
        val instance = AnglePropType()

        fun create(a: Prop<Double>, degrees: Boolean): Prop<Angle> {
            if (a.isConstant()) {
                if (degrees) {
                    return PropConstant(Angle.degrees(a.value))
                } else {
                    return PropConstant(Angle.radians(a.value))
                }
            } else {
                if (degrees) {
                    return PropCalculation1(a) { av -> Angle.degrees(av) }
                } else {
                    return PropCalculation1(a) { av -> Angle.radians(av) }
                }
            }
        }
    }
}


class AngleExpression(expression: String, context: Context = constantsContext)
    : PropExpression<Angle>(expression, Angle::class, context)

fun degConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return AnglePropType.create(a as Prop<Double>, degrees = true)
    }
    return throwExpectedType("Double", a)
}

fun radConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return AnglePropType.create(a as Prop<Double>, degrees = false)
    }
    return throwExpectedType("Double", a)
}
