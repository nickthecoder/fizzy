package uk.co.nickthecoder.fizzy.prop

class DoubleProp(initialValue: Double = 0.0) : Prop<Double>(initialValue)

class DoublePlus(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b, 0.0) {

    override fun eval() {
        value = a.value + b.value
    }
}

class DoubleMinus(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b, 0.0) {

    override fun eval() {
        value = a.value - b.value
    }
}

class DoubleTimes(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b, 0.0) {

    override fun eval() {
        value = a.value * b.value
    }
}

class DoubleDiv(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b, 0.0) {

    override fun eval() {
        value = a.value / b.value
    }
}

class DoubleSqrt(a: Prop<Double>) : UnaryPropCalculation<Double>(a, 0.0) {

    override fun eval() {
        value = Math.sqrt(a.value)
    }
}
