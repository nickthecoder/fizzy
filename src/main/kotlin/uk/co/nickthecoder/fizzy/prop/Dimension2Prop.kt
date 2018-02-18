package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.Field
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2

interface Dimension2Prop : Prop<Dimension2> {
    override fun findField(name: String): Prop<*>? {

        return when (name) {
            "x" -> Field<Dimension2, Dimension>(this) { value.x }
            "y" -> Field<Dimension2, Dimension>(this) { value.y }
            else -> null
        }
    }
}

class Dimension2Expression(expression: String, context: Context = constantsContext)
    : ExpressionProp<Dimension2>(expression, Dimension2::class, context), Dimension2Prop

class Dimension2Constant(value: Dimension2 = Dimension2.ZERO)
    : Dimension2Prop, PropConstant<Dimension2>(value) {

    companion object {
        fun create(a: Prop<Dimension>, b: Prop<Dimension>): Dimension2Prop {
            if (a is PropConstant<Dimension> && b is PropConstant<Dimension>) {
                return Dimension2Constant(Dimension2(a.value, b.value))
            } else {
                return Dimension2PropLinked(a, b)
            }
        }
    }
}

class Dimension2PropLinked(val x: Prop<Dimension>, val y: Prop<Dimension>)
    : Dimension2Prop, PropCalculation<Dimension2>(), PropListener<Dimension> {

    init {
        x.listeners.add(this)
        y.listeners.add(this)
    }

    override fun eval() = Dimension2(x.value, y.value)

    override fun dirty(prop: Prop<Dimension>) {
        dirty = true
    }
}

class Dimension2Plus(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : Dimension2Prop, BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value + b.value
}

class Dimension2Minus(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : Dimension2Prop, BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value - b.value
}

class Dimension2UnaryMinus(a: Prop<Dimension2>)
    : Dimension2Prop, UnaryPropCalculation<Dimension2>(a) {

    override fun eval() = Dimension2(-a.value.x, -a.value.y)
}

class Dimension2Times(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : Dimension2Prop, BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value * b.value
}

class Dimension2TimesDouble(a: Prop<Dimension2>, b: Prop<Double>)
    : Dimension2Prop, GenericBinaryPropCalculation<Dimension2, Dimension2, Double>(a, b) {

    override fun eval() = a.value * b.value
}

class Dimension2TimesVector2(a: Prop<Dimension2>, b: Prop<Vector2>)
    : Dimension2Prop, GenericBinaryPropCalculation<Dimension2, Dimension2, Vector2>(a, b) {

    override fun eval() = a.value * b.value
}

class Dimension2Div(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : Dimension2Prop, BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value / b.value
}

class Dimension2DivVector2(a: Prop<Dimension2>, b: Prop<Vector2>)
    : Dimension2Prop, GenericBinaryPropCalculation<Dimension2, Dimension2, Vector2>(a, b) {

    override fun eval() = a.value / b.value
}

class Dimension2DivDouble(a: Prop<Dimension2>, b: Prop<Double>)
    : Dimension2Prop, GenericBinaryPropCalculation<Dimension2, Dimension2, Double>(a, b) {

    override fun eval() = a.value / b.value
}

class Dimension2Ratio(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Dimension2, Dimension2>(a, b) {
    override fun eval(): Vector2 {
        assert(a.value.x.power == b.value.x.power)
        assert(a.value.y.power == b.value.y.power)
        return Vector2(
                a.value.x.inDefaultUnits / b.value.x.inDefaultUnits,
                a.value.y.inDefaultUnits / b.value.y.inDefaultUnits
        )
    }
}

class NewDimension2 : FunctionDimensionDimension() {
    override fun callDD(a: Prop<Dimension>, b: Prop<Dimension>): Prop<*> {
        return Dimension2Constant.create(a, b)
    }
}
