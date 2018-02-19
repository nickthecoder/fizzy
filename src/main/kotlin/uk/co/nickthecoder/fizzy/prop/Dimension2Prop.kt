package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2

class Dimension2PropType : PropType<Dimension2>(Dimension2::class) {

    override fun findField(prop: Prop<Dimension2>, name: String): Prop<*>? {

        return when (name) {
            "x" -> PropField<Dimension2, Dimension>(prop) { prop.value.x }
            "y" -> PropField<Dimension2, Dimension>(prop) { prop.value.y }
            else -> null
        }
    }
}

class Dimension2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Dimension2>(expression, Dimension2::class, context)

class Dimension2Constant(value: Dimension2 = Dimension2.ZERO)
    : PropConstant<Dimension2>(value) {

    companion object {
        fun create(a: Prop<Dimension>, b: Prop<Dimension>): Prop<Dimension2> {
            if (a is PropConstant<Dimension> && b is PropConstant<Dimension>) {
                return Dimension2Constant(Dimension2(a.value, b.value))
            } else {
                return Dimension2PropLinked(a, b)
            }
        }
    }
}

class Dimension2PropLinked(val x: Prop<Dimension>, val y: Prop<Dimension>)
    : PropCalculation<Dimension2>(), PropListener<Dimension> {

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
    : BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value + b.value
}

class Dimension2Minus(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value - b.value
}

class Dimension2UnaryMinus(a: Prop<Dimension2>)
    : UnaryPropCalculation<Dimension2>(a) {

    override fun eval() = Dimension2(-a.value.x, -a.value.y)
}

class Dimension2Times(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value * b.value
}

class Dimension2TimesDouble(a: Prop<Dimension2>, b: Prop<Double>)
    : GenericBinaryPropCalculation<Dimension2, Dimension2, Double>(a, b) {

    override fun eval() = a.value * b.value
}

class Dimension2TimesVector2(a: Prop<Dimension2>, b: Prop<Vector2>)
    : GenericBinaryPropCalculation<Dimension2, Dimension2, Vector2>(a, b) {

    override fun eval() = a.value * b.value
}

class Dimension2Div(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : BinaryPropCalculation<Dimension2>(a, b) {

    override fun eval() = a.value / b.value
}

class Dimension2DivVector2(a: Prop<Dimension2>, b: Prop<Vector2>)
    : GenericBinaryPropCalculation<Dimension2, Dimension2, Vector2>(a, b) {

    override fun eval() = a.value / b.value
}

class Dimension2DivDouble(a: Prop<Dimension2>, b: Prop<Double>)
    : GenericBinaryPropCalculation<Dimension2, Dimension2, Double>(a, b) {

    override fun eval() = a.value / b.value
}

class Dimension2Ratio(a: Prop<Dimension2>, b: Prop<Dimension2>)
    : GenericBinaryPropCalculation<Vector2, Dimension2, Dimension2>(a, b) {
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
