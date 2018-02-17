package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.FunctionDoubleDouble
import uk.co.nickthecoder.fizzy.model.Vector2

interface Vector2Prop : Prop<Vector2>

class Vector2Constant(value: Vector2 = Vector2.ZERO)
    : Vector2Prop, PropConstant<Vector2>(value) {

    companion object {
        fun create(x: Prop<Double>, y: Prop<Double>): Prop<Vector2> {
            if (x is PropConstant<Double> && y is PropConstant<Double>) {
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

    override fun eval() {
        calculatedValue = Vector2(x.value, y.value)
    }

    override fun dirty(prop: Prop<Double>) {
        dirty = true
    }
}

class Vector2Plus(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class Vector2Minus(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class Vector2Times(a: Prop<Vector2>, b: Prop<Vector2>)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2TimesDouble(a: Prop<Vector2>, b: Prop<Double>)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2Div(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class Vector2DivDouble(a: Prop<Vector2>, b: Prop<Double>)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}


class NewVector2 : FunctionDoubleDouble("Vector2") {
    override fun callDD(a: Prop<Double>, b: Prop<Double>): Prop<*> {
        return Vector2Constant.create(a, b)
    }
}
