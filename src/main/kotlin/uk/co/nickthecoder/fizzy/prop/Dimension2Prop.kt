package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Function2
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2

class Dimension2Prop(initialValue: Dimension2 = Dimension2.ZERO) : PropValue<Dimension2>(initialValue)

class LinkedDimension2(val x: Prop<Dimension>, val y: Prop<Dimension>)
    : PropCalculation<Dimension2>(Dimension2(x.value, y.value)), PropListener<Dimension> {

    init {
        x.listeners.add(this)
        y.listeners.add(this)
    }

    override fun eval() {
        calculatedValue = Dimension2(x.value, y.value)
    }

    override fun dirty(prop: Prop<Dimension>) {
        dirty = true
    }
}

class Dimension2Plus(a: Prop<Dimension2>, b: Prop<Dimension2>) : BinaryPropCalculation<Dimension2>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class Dimension2Minus(a: Prop<Dimension2>, b: Prop<Dimension2>) : BinaryPropCalculation<Dimension2>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class Dimension2UnaryMinus(a: Prop<Dimension2>) : UnaryPropCalculation<Dimension2>(a, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = Dimension2(
                Dimension(-a.value.x.number, a.value.x.units, a.value.x.power),
                Dimension(-a.value.y.number, a.value.y.units, a.value.y.power)
        )
    }
}

class Dimension2Times(a: Prop<Dimension2>, b: Prop<Dimension2>) : BinaryPropCalculation<Dimension2>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Dimension2TimesDouble(a: Prop<Dimension2>, b: Prop<Double>) : GenericBinaryPropCalculation<Dimension2, Dimension2, Double>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Dimension2TimesVector2(a: Prop<Dimension2>, b: Prop<Vector2>) : GenericBinaryPropCalculation<Dimension2, Dimension2, Vector2>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Dimension2Div(a: Prop<Dimension2>, b: Prop<Dimension2>) : BinaryPropCalculation<Dimension2>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class Dimension2DivVector2(a: Prop<Dimension2>, b: Prop<Vector2>) : GenericBinaryPropCalculation<Dimension2, Dimension2, Vector2>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class Dimension2DivDouble(a: Prop<Dimension2>, b: Prop<Double>) : GenericBinaryPropCalculation<Dimension2, Dimension2, Double>(a, b, Dimension2.ZERO) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}


abstract class FunctionDimension2Dimension2(name: String)
    : Function2(name) {

    override fun call(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Dimension && b.value is Dimension2) {
            @Suppress("UNCHECKED_CAST")
            return callD2D2(a as Prop<Dimension2>, b as Prop<Dimension2>)
        } else {
            throw RuntimeException("Expected arguments (Dimension2,Dimension2)")
        }
    }

    abstract fun callD2D2(a: Prop<Dimension2>, b: Prop<Dimension2>): Prop<*>
}

class Dimension2Ratio(a: Prop<Dimension2>, b: Prop<Dimension2>) : GenericBinaryPropCalculation<Vector2, Dimension2, Dimension2>(a, b, Vector2.ZERO) {
    override fun eval() {
        assert(a.value.x.power == b.value.x.power)
        assert(a.value.y.power == b.value.y.power)
        calculatedValue = Vector2(
                a.value.x.number / b.value.x.inUnits(a.value.x.units),
                a.value.y.number / b.value.y.inUnits(a.value.y.units)
        )
    }
}


class NewDimension2 : FunctionDimensionDimension("Dimension2") {
    override fun callDD(a: Prop<Dimension>, b: Prop<Dimension>): Prop<*> {
        return LinkedDimension2(a, b)
    }
}