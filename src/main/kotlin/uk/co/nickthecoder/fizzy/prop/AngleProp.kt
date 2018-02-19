package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Angle

class AnglePropType : PropType<Angle>(Angle::class) {

    override fun findField(prop: Prop<Angle>, name: String): PropField<Angle, *>? {
        return when (name) {
            "degrees" -> PropField<Angle, Double>(prop) { prop.value.degrees }
            "radians" -> PropField<Angle, Double>(prop) { prop.value.radians }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<Angle>, name: String): PropMethod<Angle, *>? {
        return null
    }

}


class AngleExpression(expression: String, context: Context = constantsContext)
    : PropExpression<Angle>(expression, Angle::class, context)

class AngleConstant(value: Angle = Angle.ZERO)
    : PropConstant<Angle>(value) {

    companion object {
        fun create(a: Prop<Double>, degrees: Boolean): Prop<Angle> {
            if (a is DoubleConstant) {
                if (degrees) {
                    return AngleConstant(Angle.degrees(a.value))
                } else {
                    return AngleConstant(Angle.radians(a.value))
                }
            } else {
                if (degrees) {
                    return AnglePropLinked(DoubleTimes(a, DoubleConstant(Math.PI / 180.0)))
                } else {
                    return AnglePropLinked(a)
                }
            }
        }
    }
}

class AnglePropLinked(val radians: Prop<Double>)
    : PropCalculation<Angle>() {

    init {
        radians.listeners.add(this)
    }

    override fun eval() = Angle.radians(radians.value)
}

class AnglePlus(a: Prop<Angle>, b: Prop<Angle>)
    : BinaryPropCalculation<Angle>(a, b) {

    override fun eval() = Angle.radians(a.value.radians + b.value.radians)
}

class AngleMinus(a: Prop<Angle>, b: Prop<Angle>)
    : BinaryPropCalculation<Angle>(a, b) {

    override fun eval() = Angle.radians(a.value.radians - b.value.radians)
}

class AngleUnaryMinus(a: Prop<Angle>)
    : UnaryPropCalculation<Angle>(a) {

    override fun eval() = Angle.radians(-a.value.radians)
}

class AngleTimesDouble(a: Prop<Angle>, b: Prop<Double>)
    : GenericBinaryPropCalculation<Angle, Angle, Double>(a, b) {

    override fun eval() = Angle.radians(a.value.radians * b.value)
}

class AngleDiv(a: Prop<Angle>, b: Prop<Angle>)
    : GenericBinaryPropCalculation<Double, Angle, Angle>(a, b) {

    override fun eval() = a.value.radians / b.value.radians
}


class AngleDivDouble(a: Prop<Angle>, b: Prop<Double>)
    : GenericBinaryPropCalculation<Angle, Angle, Double>(a, b) {
    override fun eval() = Angle.radians(a.value.radians / b.value)
}

fun degConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return AngleConstant.create(a as Prop<Double>, degrees = true)
    }
    return throwExpectedType("Double", a)
}

fun radConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return AngleConstant.create(a as Prop<Double>, degrees = false)
    }
    return throwExpectedType("Double", a)
}
