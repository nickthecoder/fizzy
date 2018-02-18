package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.prop.*


abstract class Function() : AbstractProp<String>() {

    override val value: String
        get() = javaClass.simpleName

    abstract fun call(args: Prop<*>): Prop<*>
}

class Sqrt : Function() {
    override fun call(args: Prop<*>): Prop<*> {
        if (args.value is Double) {
            @Suppress("UNCHECKED_CAST")
            return DoubleSqrt(args as Prop<Double>)
        } else if (args.value is Dimension) {
            @Suppress("UNCHECKED_CAST")
            return DimensionSqrt(args as Prop<Dimension>)
        } else {
            throw RuntimeException("Expected Double, Angle or Dimension but found ${args.value?.javaClass}")
        }
    }
}


class Ratio : Function() {
    override fun call(args: Prop<*>): Prop<*> {
        if (args is ArgList && args.value.size == 2) {
            val a = args.value[0]
            val b = args.value[1]

            if (a.value is Dimension && b.value is Dimension) {
                @Suppress("UNCHECKED_CAST")
                return DimensionRatio(a as Prop<Dimension>, b as Prop<Dimension>)

            } else if (a.value is Dimension2 && b.value is Dimension2) {
                @Suppress("UNCHECKED_CAST")
                return Dimension2Ratio(a as Prop<Dimension2>, b as Prop<Dimension2>)
            }
        }
        throw RuntimeException("Expected (Dimension, Dimension) or (Dimension2, Dimension2)")
    }
}

abstract class Function2() : Function() {
    override fun call(args: Prop<*>): Prop<*> {
        if (args is ArgList) {
            if (args.value.size != 2) {
                throw RuntimeException("Expected 2 parameters but found ${args.value.size}")
            }
            return call(args.value[0], args.value[1])
        } else {
            throw RuntimeException("Expected 2 parameters but found 1")
        }
    }

    abstract fun call(a: Prop<*>, b: Prop<*>): Prop<*>
}

abstract class FunctionDouble() : Function() {

    override fun call(args: Prop<*>): Prop<*> {
        if (args.value is Double) {
            @Suppress("UNCHECKED_CAST")
            return callD(args as Prop<Double>)
        } else {
            throw RuntimeException("Expected Double, but found ${args.value?.javaClass}")
        }
    }

    abstract fun callD(a: Prop<Double>): Prop<*>

}

abstract class FunctionAngle() : Function() {

    override fun call(args: Prop<*>): Prop<*> {
        if (args.value is Angle) {
            @Suppress("UNCHECKED_CAST")
            return callAngle(args as Prop<Angle>)
        } else {
            throw RuntimeException("Expected Angle, but found ${args.value?.javaClass}")
        }
    }

    abstract fun callAngle(a: Prop<Angle>): Prop<*>

}

abstract class FunctionDoubleDouble() : Function2() {
    override fun call(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            @Suppress("UNCHECKED_CAST")
            return callDD(a as Prop<Double>, b as Prop<Double>)
        } else {
            throw RuntimeException("Expected arguments (Double,Double)")
        }
    }

    abstract fun callDD(a: Prop<Double>, b: Prop<Double>): Prop<*>
}
