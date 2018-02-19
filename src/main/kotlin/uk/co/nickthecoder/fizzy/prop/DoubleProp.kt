package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext

class DoublePropType : PropType<Double>(Double::class) {

    override fun findField(prop: Prop<Double>, name: String): PropField<Double, *>? {
        return null
    }

    override fun findMethod(prop: Prop<Double>, name: String): PropMethod<Double, *>? {
        return when (name) {
            "abs" -> PropMethod0(prop) { Math.abs(prop.value) }
            else -> null
        }
    }
}

class DoubleExpression(expression: String, context: Context = constantsContext)
    : PropExpression<Double>(expression, Double::class, context)

class DoubleConstant(value: Double = 0.0)
    : PropConstant<Double>(value)

class DoublePlus(a: Prop<Double>, b: Prop<Double>)
    : BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value + b.value
}

class DoubleMinus(a: Prop<Double>, b: Prop<Double>)
    : BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value - b.value
}

class DoubleTimes(a: Prop<Double>, b: Prop<Double>)
    : BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value * b.value
}

class DoubleDiv(a: Prop<Double>, b: Prop<Double>)
    : BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value / b.value
}

class DoubleSqrt(a: Prop<Double>)
    : UnaryPropCalculation<Double>(a) {

    override fun eval() = Math.sqrt(a.value)
}
