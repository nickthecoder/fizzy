package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Vector2

class Vector2Prop(initialValue: Vector2 = Vector2.ZERO) : PropValue<Vector2>(initialValue)

class LinkedVector2(val x: Prop<Double>, val y: Prop<Double>)
    : PropCalculation<Vector2>(Vector2(x.value, y.value)), PropListener<Double> {

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

class Vector2Plus(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropCalculation<Vector2>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class Vector2Minus(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropCalculation<Vector2>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class Vector2Scale(a: Prop<Vector2>, b: Prop<Double>) : GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2Shrink(a: Prop<Vector2>, b: Prop<Double>) : GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class Vector2Times(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropCalculation<Vector2>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2TimesDouble(a: Prop<Vector2>, b: Prop<Double>) : GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class Vector2Div(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropCalculation<Vector2>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}


class Vector2DivDouble(a: Prop<Vector2>, b: Prop<Double>) : GenericBinaryPropCalculation<Vector2, Vector2, Double>(a, b, Vector2.ZERO) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}