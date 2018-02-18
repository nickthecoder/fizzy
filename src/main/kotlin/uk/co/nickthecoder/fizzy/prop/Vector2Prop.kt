package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.Field
import uk.co.nickthecoder.fizzy.evaluator.FunctionDoubleDouble
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Vector2

interface Vector2Prop : Prop<Vector2>

class Vector2PropType : PropType<Vector2>(Vector2::class) {

    override fun findField(prop: Prop<Vector2>, name: String): Prop<*>? {
        return when (name) {
            "x" -> Field<Vector2, Double>(prop) { prop.value.x }
            "y" -> Field<Vector2, Double>(prop) { prop.value.y }
            else -> null
        }
    }
}

class Vector2Expression(expression: String, context: Context = constantsContext)
    : ExpressionProp<Vector2>(expression, Vector2::class, context), Vector2Prop

class Vector2Constant(value: Vector2 = Vector2.ZERO)
    : Vector2Prop, PropConstant<Vector2>(value) {

    companion object {
        fun create(x: Prop<Double>, y: Prop<Double>): Prop<Vector2> {
            if (x is DoubleConstant && y is DoubleConstant) {
                return Vector2Constant(Vector2(x.value, y.value))
            } else {
                return Vector2PropLinked(x, y)
            }
        }
    }
}

class Vector2PropLinked(val x: Prop<Double>, val y: Prop<Double>)
    : Vector2Prop, PropCalculation<Vector2>(), PropListener<Double> {

    init {
        x.listeners.add(this)
        y.listeners.add(this)
    }

    override fun eval() = Vector2(x.value, y.value)

    override fun dirty(prop: Prop<Double>) {
        dirty = true
    }
}

class Vector2Plus(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value + b.value
}

class Vector2Minus(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value - b.value
}

class Vector2Times(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value * b.value
}

class Vector2TimesDouble(a: Prop<Vector2>, b: Prop<Double>)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() = a.value * b.value
}

class Vector2Div(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value / b.value
}

class Vector2DivDouble(a: Prop<Vector2>, b: Prop<Double>)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() = a.value / b.value
}


class NewVector2 : FunctionDoubleDouble() {
    override fun callDD(a: Prop<Double>, b: Prop<Double>): Prop<*> {
        return Vector2Constant.create(a, b)
    }
}
