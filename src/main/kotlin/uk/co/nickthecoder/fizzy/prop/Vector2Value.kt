package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Vector2

class Vector2Value(initialValue: Vector2 = Vector2.zero) : Prop<Vector2>(initialValue)


class Vector2Plus(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropExpression<Vector2>(a, b, Vector2.zero) {

    override fun eval() {
        value = a.value + b.value
    }
}

class Vector2Minus(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropExpression<Vector2>(a, b, Vector2.zero) {

    override fun eval() {
        value = a.value - b.value
    }
}

class Vector2Scale(a: Prop<Vector2>, b: Prop<Double>) : GenericBinaryPropExpression<Vector2, Vector2, Double>(a, b, Vector2.zero) {

    override fun eval() {
        value = a.value * b.value
    }
}

class Vector2Shrink(a: Prop<Vector2>, b: Prop<Double>) : GenericBinaryPropExpression<Vector2, Vector2, Double>(a, b, Vector2.zero) {

    override fun eval() {
        value = a.value / b.value
    }
}

class Vector2Times(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropExpression<Vector2>(a, b, Vector2.zero) {

    override fun eval() {
        value = a.value * b.value
    }
}

class Vector2Div(a: Prop<Vector2>, b: Prop<Vector2>) : BinaryPropExpression<Vector2>(a, b, Vector2.zero) {

    override fun eval() {
        value = a.value / b.value
    }
}
