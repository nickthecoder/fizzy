package uk.co.nickthecoder.fizzy.prop

interface DoubleProp : Prop<Double>

class DoubleConstant(value: Double = 0.0)
    : DoubleProp, PropConstant<Double>(value)

class DoublePlus(a: DoubleProp, b: DoubleProp)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class DoubleMinus(a: DoubleProp, b: DoubleProp)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class DoubleTimes(a: DoubleProp, b: DoubleProp)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class DoubleDiv(a: DoubleProp, b: DoubleProp)
    : DoubleProp, BinaryPropCalculation<Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DoubleSqrt(a: DoubleProp)
    : DoubleProp, UnaryPropCalculation<Double>(a) {

    override fun eval() {
        calculatedValue = Math.sqrt(a.value)
    }
}
