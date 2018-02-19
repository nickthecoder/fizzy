package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.FunctionDoubleDouble
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
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
            else -> null
        }
    }
}

class Vector2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Vector2>(expression, Vector2::class, context)

class Vector2Constant(value: Vector2 = Vector2.ZERO)
    : PropConstant<Vector2>(value) {

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
    : PropCalculation<Vector2>() {

    init {
        x.listeners.add(this)
        y.listeners.add(this)
    }

    override fun eval() = Vector2(x.value, y.value)
}

class Vector2Plus(a: Prop<Vector2>, b: Prop<Vector2>)
    : BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value + b.value
}

class Vector2Minus(a: Prop<Vector2>, b: Prop<Vector2>)
    : BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value - b.value
}

class Vector2Times(a: Prop<Vector2>, b: Prop<Vector2>)
    : BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value * b.value
}

class Vector2TimesDouble(a: Prop<Vector2>, b: Prop<Double>)
    : GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() = a.value * b.value
}

class Vector2Div(a: Prop<Vector2>, b: Prop<Vector2>)
    : BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() = a.value / b.value
}

class Vector2DivDouble(a: Prop<Vector2>, b: Prop<Double>)
    : GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() = a.value / b.value
}

class NewVector2 : FunctionDoubleDouble() {
    override fun callDD(a: Prop<Double>, b: Prop<Double>): Prop<*> {
        return Vector2Constant.create(a, b)
    }
}
