package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Function2
import uk.co.nickthecoder.fizzy.model.Dimension

class DimensionConstant(value: Dimension = Dimension.ZERO_mm)
    : PropConstant<Dimension>(value) {

    companion object {
        fun create(a: Prop<Double>, units: Dimension.Units, power: Double = 1.0): Prop<Dimension> {
            if (a is PropConstant<Double>) {
                return DimensionConstant(Dimension(a.value, units, power))
            } else {
                return DimensionPropLinked(a, units, power)
            }
        }
    }
}

class DimensionPropLinked(val number: Prop<Double>, val units: Dimension.Units, val power: Double = 1.0)
    : PropCalculation<Dimension>(), PropListener<Double> {

    init {
        number.listeners.add(this)
    }

    override fun eval() {
        calculatedValue = Dimension(number.value, units, power)
    }

    override fun dirty(prop: Prop<Double>) {
        dirty = true
    }
}

class DimensionPlus(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class DimensionMinus(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class DimensionUnaryMinus(a: Prop<Dimension>) : UnaryPropCalculation<Dimension>(a) {

    override fun eval() {
        calculatedValue = -a.value
    }
}

class DimensionTimes(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class DimensionTimesDouble(a: Prop<Dimension>, b: Prop<Double>) : GenericBinaryPropCalculation<Dimension, Dimension, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}


class DimensionDiv(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DimensionDivDouble(a: Prop<Dimension>, b: Prop<Double>) : GenericBinaryPropCalculation<Dimension, Dimension, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DimensionSqrt(a: Prop<Dimension>) : UnaryPropCalculation<Dimension>(a) {

    override fun eval() {
        calculatedValue = Dimension(Math.sqrt(a.value.inUnits(a.value.units)), a.value.units, a.value.power / 2)
    }
}

abstract class FunctionDimensionDimension(name: String)
    : Function2(name) {

    override fun call(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Dimension && b.value is Dimension) {
            @Suppress("UNCHECKED_CAST")
            return callDD(a as Prop<Dimension>, b as Prop<Dimension>)
        } else {
            throw RuntimeException("Expected arguments (Dimension,Dimension)")
        }
    }

    abstract fun callDD(a: Prop<Dimension>, b: Prop<Dimension>): Prop<*>
}

class DimensionRatio(a: Prop<Dimension>, b: Prop<Dimension>) : GenericBinaryPropCalculation<Double, Dimension, Dimension>(a, b) {
    override fun eval() {
        assert(a.value.power == b.value.power)
        calculatedValue = a.value.inDefaultUnits / b.value.inDefaultUnits
    }
}

fun dimensionConversion(a: Prop<*>, units: Dimension.Units, power: Double = 1.0): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return DimensionConstant.create(a as Prop<Double>, units, power)
    }
    return throwExpectedType("Double", a)
}

fun mmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.mm)
fun cmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.cm)
fun mConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.m)
fun kmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.km)
