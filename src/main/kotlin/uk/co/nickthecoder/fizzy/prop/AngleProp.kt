package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Angle

interface AngleProp : Prop<Angle>

class AngleConstant(value: Angle = Angle.ZERO)
    : AngleProp, PropConstant<Angle>(value) {

    companion object {
        fun create(a: DoubleProp, degrees: Boolean): AngleProp {
            if (a is DoubleConstant) {
                if (degrees) {
                    return AngleConstant(Angle.degrees(a.value))
                } else {
                    return AngleConstant(Angle.radians(a.value))
                }
            } else {
                if (degrees) {
                    return AnglePropLinked(DoubleTimes(a, DoubleConstant(Math.PI / 180.0)))
                } else {
                    return AnglePropLinked(a)
                }
            }
        }
    }
}

class AnglePropLinked(val radians: DoubleProp)
    : AngleProp, PropCalculation<Angle>(), PropListener<Double> {

    init {
        radians.listeners.add(this)
    }

    override fun eval() {
        calculatedValue = Angle.radians(radians.value)
    }

    override fun dirty(prop: Prop<Double>) {
        dirty = true
    }
}

class AnglePlus(a: AngleProp, b: AngleProp)
    : AngleProp, BinaryPropCalculation<Angle>(a, b) {

    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians + b.value.radians)
    }
}

class AngleMinus(a: AngleProp, b: AngleProp)
    : AngleProp, BinaryPropCalculation<Angle>(a, b) {

    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians - b.value.radians)
    }
}

class AngleUnaryMinus(a: AngleProp)
    : AngleProp, UnaryPropCalculation<Angle>(a) {

    override fun eval() {
        calculatedValue = Angle.radians(-a.value.radians)
    }
}

class AngleTimesDouble(a: AngleProp, b: DoubleProp)
    : AngleProp, GenericBinaryPropCalculation<Angle, Angle, Double>(a, b) {

    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians * b.value)
    }
}

class AngleDiv(a: AngleProp, b: AngleProp)
    : DoubleProp, GenericBinaryPropCalculation<Double, Angle, Angle>(a, b) {

    override fun eval() {
        calculatedValue = a.value.radians / b.value.radians
    }
}

class AngleDivDouble(a: AngleProp, b: DoubleProp)
    : AngleProp, GenericBinaryPropCalculation<Angle, Angle, Double>(a, b) {
    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians / b.value)
    }
}

fun degConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        return AngleConstant.create(a as DoubleProp, degrees = true)
    }
    return throwExpectedType("Double", a)
}

fun radConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        return AngleConstant.create(a as DoubleProp, degrees = false)
    }
    return throwExpectedType("Double", a)
}
