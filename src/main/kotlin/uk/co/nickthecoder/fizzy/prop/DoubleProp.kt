package uk.co.nickthecoder.fizzy.prop

class DoubleProp(initialValue: Double = 0.0) : PropValue<Double>(initialValue)

class DoublePlus(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class DoubleMinus(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class DoubleTimes(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class DoubleDiv(a: Prop<Double>, b: Prop<Double>) : BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DoubleSqrt(a: Prop<Double>) : UnaryPropCalculation<Double>(a) {

    override fun eval() {
        calculatedValue = Math.sqrt(a.value)
    }
}
