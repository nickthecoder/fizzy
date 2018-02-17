package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.FunctionDouble
import uk.co.nickthecoder.fizzy.model.Angle

interface AngleProp : Prop<Angle>

class AngleConstant(value: Angle = Angle.ZERO)
    : AngleProp, PropConstant<Angle>(value) {

    companion object {
        fun create(a: Prop<Double>): Prop<Angle> {
            if (a is PropConstant<Double>) {
                return AngleConstant(Angle.radians(a.value))
            } else {
                return AnglePropLinked(a)
            }
        }
    }
}

class AnglePropLinked(val radians: Prop<Double>)
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

class AnglePlus(a: Prop<Angle>, b: Prop<Angle>)
    : AngleProp, BinaryPropCalculation<Angle>(a, b) {

    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians + b.value.radians)
    }
}

class AngleMinus(a: Prop<Angle>, b: Prop<Angle>)
    : AngleProp, BinaryPropCalculation<Angle>(a, b) {

    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians - b.value.radians)
    }
}

class AngleUnaryMinus(a: Prop<Angle>)
    : AngleProp, UnaryPropCalculation<Angle>(a) {

    override fun eval() {
        calculatedValue = Angle.radians(-a.value.radians)
    }
}

class AngleTimesDouble(a: Prop<Angle>, b: Prop<Double>)
    : AngleProp, GenericBinaryPropCalculation<Angle, Angle, Double>(a, b) {

    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians * b.value)
    }
}

class AngleDiv(a: Prop<Angle>, b: Prop<Angle>)
    : DoubleProp, GenericBinaryPropCalculation<Double, Angle, Angle>(a, b) {

    override fun eval() {
        calculatedValue = a.value.radians / b.value.radians
    }
}

class AngleDivDouble(a: Prop<Angle>, b: Prop<Double>)
    : AngleProp, GenericBinaryPropCalculation<Angle, Angle, Double>(a, b) {
    override fun eval() {
        calculatedValue = Angle.radians(a.value.radians / b.value)
    }
}


class NewAngle : FunctionDouble("Angle") {
    override fun callD(a: Prop<Double>): Prop<*> {
        return AngleConstant.create(a)
    }
}

fun degConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        return AngleConstant(Angle.degrees(a.value as Double))
    }
    return throwExpectedType("Double", a)
}

fun radConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        return AngleConstant(Angle.radians(a.value as Double))
    }
    return throwExpectedType("Double", a)
}
