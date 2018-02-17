package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Function2
import uk.co.nickthecoder.fizzy.model.Dimension

interface DimensionProp : Prop<Dimension>

class DimensionConstant(value: Dimension = Dimension.ZERO_mm)
    : DimensionProp, PropConstant<Dimension>(value) {

    companion object {
        fun create(a: DoubleProp, units: Dimension.Units, power: Double = 1.0): DimensionProp {
            if (a is DoubleConstant) {
                return DimensionConstant(Dimension(a.value, units, power))
            } else {
                return DimensionPropLinked(a, units, power)
            }
        }
    }
}

class DimensionPropLinked(val number: DoubleProp, val units: Dimension.Units, val power: Double = 1.0)
    : DimensionProp, PropCalculation<Dimension>(), PropListener<Double> {

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

class DimensionPlus(a: DimensionProp, b: DimensionProp)
    : DimensionProp, BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class DimensionMinus(a: DimensionProp, b: DimensionProp)
    : DimensionProp, BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class DimensionUnaryMinus(a: DimensionProp)
    : DimensionProp, UnaryPropCalculation<Dimension>(a) {

    override fun eval() {
        calculatedValue = -a.value
    }
}

class DimensionTimes(a: DimensionProp, b: DimensionProp)
    : DimensionProp, BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class DimensionTimesDouble(a: DimensionProp, b: DoubleProp)
    : DimensionProp, GenericBinaryPropCalculation<Dimension, Dimension, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}


class DimensionDiv(a: DimensionProp, b: DimensionProp)
    : DimensionProp, BinaryPropCalculation<Dimension>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DimensionDivDouble(a: DimensionProp, b: DoubleProp)
    : DimensionProp, GenericBinaryPropCalculation<Dimension, Dimension, Double>(a, b) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DimensionSqrt(a: DimensionProp)
    : DimensionProp, UnaryPropCalculation<Dimension>(a) {

    override fun eval() {
        calculatedValue = Dimension(Math.sqrt(a.value.inUnits(a.value.units)), a.value.units, a.value.power / 2)
    }
}

abstract class FunctionDimensionDimension(name: String)
    : Function2(name) {

    override fun call(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Dimension && b.value is Dimension) {
            @Suppress("UNCHECKED_CAST")
            return callDD(a as DimensionProp, b as DimensionProp)
        } else {
            throw RuntimeException("Expected arguments (Dimension,Dimension)")
        }
    }

    abstract fun callDD(a: DimensionProp, b: DimensionProp): Prop<*>
}

class DimensionRatio(a: DimensionProp, b: DimensionProp)
    : DoubleProp, GenericBinaryPropCalculation<Double, Dimension, Dimension>(a, b) {

    override fun eval() {
        assert(a.value.power == b.value.power)
        calculatedValue = a.value.inDefaultUnits / b.value.inDefaultUnits
    }
}

fun dimensionConversion(a: Prop<*>, units: Dimension.Units, power: Double = 1.0): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return DimensionConstant.create(a as DoubleProp, units, power)
    }
    return throwExpectedType("Double", a)
}

fun mmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.mm)
fun cmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.cm)
fun mConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.m)
fun kmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.km)
