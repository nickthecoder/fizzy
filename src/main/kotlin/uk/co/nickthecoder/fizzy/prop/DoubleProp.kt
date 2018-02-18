package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext

interface DoubleProp : Prop<Double> {
    override fun findField(name: String): Prop<*>? {
        return null
    }
}

class DoubleExpression(expression: String, context: Context = constantsContext)
    : ExpressionProp<Double>(expression, Double::class, context), DoubleProp

class DoubleConstant(value: Double = 0.0)
    : DoubleProp, PropConstant<Double>(value)

class DoublePlus(a: Prop<Double>, b: Prop<Double>)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value + b.value
}

class DoubleMinus(a: Prop<Double>, b: Prop<Double>)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value - b.value
}

class DoubleTimes(a: Prop<Double>, b: Prop<Double>)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value * b.value
}

class DoubleDiv(a: Prop<Double>, b: Prop<Double>)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() = a.value / b.value
}

class DoubleSqrt(a: Prop<Double>)
    : DoubleProp, UnaryPropCalculation<Double>(a) {

    override fun eval() = Math.sqrt(a.value)
}
