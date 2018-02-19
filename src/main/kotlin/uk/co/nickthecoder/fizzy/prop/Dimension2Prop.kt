package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2

class Dimension2PropType : PropType<Dimension2>(Dimension2::class) {

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
            else -> null
        }
    }
}

class Dimension2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Dimension2>(expression, Dimension2::class, context)

class Dimension2Constant(value: Dimension2 = Dimension2.ZERO_mm)
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
    : PropCalculation<Dimension2>() {

    init {
        x.listeners.add(this)
        y.listeners.add(this)
    }

    override fun eval() = Dimension2(x.value, y.value)
}

class NewDimension2 : FunctionDimensionDimension() {
    override fun callDD(a: Prop<Dimension>, b: Prop<Dimension>): Prop<*> {
        return Dimension2Constant.create(a, b)
    }
}
