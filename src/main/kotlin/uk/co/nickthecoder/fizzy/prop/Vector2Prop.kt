package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.FunctionDoubleDouble
import uk.co.nickthecoder.fizzy.model.Vector2

interface Vector2Prop : Prop<Vector2>

class Vector2Constant(value: Vector2 = Vector2.ZERO)
    : Vector2Prop, PropConstant<Vector2>(value) {

    companion object {
        fun create(x: DoubleProp, y: DoubleProp): Vector2Prop {
            if (x is DoubleConstant && y is DoubleConstant) {
                return Vector2Constant(Vector2(x.value, y.value))
            } else {
                return Vector2PropLinked(x, y)
            }
        }
    }
}

class Vector2PropLinked(val x: DoubleProp, val y: DoubleProp)
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

class Vector2Plus(a: Vector2Prop, b: Vector2Prop)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class Vector2Minus(a: Vector2Prop, b: Vector2Prop)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class Vector2Times(a: Vector2Prop, b: Vector2Prop)
    : Vector2Prop, BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2TimesDouble(a: Vector2Prop, b: DoubleProp)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2Div(a: Vector2Prop, b: Vector2Prop) : BinaryPropCalculation<Vector2>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class Vector2DivDouble(a: Vector2Prop, b: DoubleProp)
    : Vector2Prop, GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}


class NewVector2 : FunctionDoubleDouble("Vector2") {
    override fun callDD(a: DoubleProp, b: DoubleProp): Prop<*> {
        return Vector2Constant.create(a, b)
    }
}
