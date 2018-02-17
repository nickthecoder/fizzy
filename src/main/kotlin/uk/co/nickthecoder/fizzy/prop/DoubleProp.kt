package uk.co.nickthecoder.fizzy.prop

interface DoubleProp : Prop<Double>

class DoubleConstant(value: Double = 0.0) : PropConstant<Double>(value)

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
