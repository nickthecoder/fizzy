package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.FunctionDouble
import uk.co.nickthecoder.fizzy.model.Angle

class AngleProp(initialValue: Angle = Angle.ZERO) : Prop<Angle>(initialValue)

class LinkedAngle(val radians: Prop<Double>)
    : PropCalculation<Angle>(Angle.radians(radians.value)), PropListener<Double> {

    init {
        radians.listeners.add(this)
    }

    override fun eval() {
        value = Angle.radians(radians.value)
    }

    override fun changed(prop: Prop<Double>) {
        dirty = true
    }
}

class AnglePlus(a: Prop<Angle>, b: Prop<Angle>) : BinaryPropCalculation<Angle>(a, b, Angle.ZERO) {

    override fun eval() {
        value = Angle.radians(a.value.radians + b.value.radians)
    }
}

class AngleMinus(a: Prop<Angle>, b: Prop<Angle>) : BinaryPropCalculation<Angle>(a, b, Angle.ZERO) {

    override fun eval() {
        value = Angle.radians(a.value.radians - b.value.radians)
    }
}

class AngleTimesDouble(a: Prop<Angle>, b: Prop<Double>) : GenericBinaryPropCalculation<Angle, Angle, Double>(a, b, Angle.ZERO) {
    override fun eval() {
        value = Angle.radians(a.value.radians * b.value)
    }
}

class DoubleTimesAngle(a: Prop<Double>, b: Prop<Angle>) : GenericBinaryPropCalculation<Angle, Double, Angle>(a, b, Angle.ZERO) {
    override fun eval() {
        value = Angle.radians(a.value * b.value.radians)
    }
}

class AngleDivDouble(a: Prop<Angle>, b: Prop<Double>) : GenericBinaryPropCalculation<Angle, Angle, Double>(a, b, Angle.ZERO) {
    override fun eval() {
        value = Angle.radians(a.value.radians / b.value)
    }
}

class NewAngle : FunctionDouble("Angle") {
    override fun callD(a: Prop<Double>): Prop<*> {
        return LinkedAngle(a)
    }
}
