package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Function2
import uk.co.nickthecoder.fizzy.model.Dimension

class DimensionProp(initialValue: Dimension = Dimension.ZERO_mm) : PropValue<Dimension>(initialValue)


class DimensionPlus(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}

class DimensionMinus(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = a.value - b.value
    }
}

class DimensionUnaryMinus(a: Prop<Dimension>) : UnaryPropCalculation<Dimension>(a, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = -a.value
    }
}

class DimensionTimes(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}

class DimensionTimesDouble(a: Prop<Dimension>, b: Prop<Double>) : GenericBinaryPropCalculation<Dimension, Dimension, Double>(a, b, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = a.value * b.value
    }
}


class DimensionDiv(a: Prop<Dimension>, b: Prop<Dimension>) : BinaryPropCalculation<Dimension>(a, b, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DimensionDivDouble(a: Prop<Dimension>, b: Prop<Double>) : GenericBinaryPropCalculation<Dimension, Dimension, Double>(a, b, Dimension.ZERO_mm) {

    override fun eval() {
        calculatedValue = a.value / b.value
    }
}

class DimensionSqrt(a: Prop<Dimension>) : UnaryPropCalculation<Dimension>(a, Dimension.ZERO_mm) {

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

class DimensionRatio(a: Prop<Dimension>, b: Prop<Dimension>) : GenericBinaryPropCalculation<Double, Dimension, Dimension>(a, b, 0.0) {
    override fun eval() {
        assert(a.value.power == b.value.power)
        calculatedValue = a.value.inDefaultUnits / b.value.inDefaultUnits
    }
}

fun dimensionConversion(a: Prop<*>, units: Dimension.Units, power: Double = 1.0): Prop<*> {
    if (a.value is Double) {
        return PropValue(Dimension(a.value as Double, units, power))
    }
    return conversionExpected("Double", a)
}

fun mmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.mm)
fun cmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.cm)
fun mConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.m)
fun kmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.km)
