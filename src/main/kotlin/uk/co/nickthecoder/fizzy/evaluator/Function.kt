package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.DoubleSqrt
import uk.co.nickthecoder.fizzy.prop.LinkedVector2
import uk.co.nickthecoder.fizzy.prop.NewAngle
import uk.co.nickthecoder.fizzy.prop.Prop


abstract class Function(name: String) : Prop<String>(name) {
    abstract fun call(args: Prop<*>): Prop<*>

    companion object {
        val functions = mutableMapOf<String, Function>()

        init {
            add(Sqrt(), NewVector2(), NewAngle())
        }

        fun find(name: String) = functions[name]

        fun add(vararg funcs: Function) {
            funcs.forEach {
                functions.put(it.value, it)
            }
        }
    }
}

abstract class FunctionDouble(name: String) : Function(name) {

    override fun call(args: Prop<*>): Prop<*> {
        if (args.value is Double) {
            return callD(args as Prop<Double>)
        } else {
            throw RuntimeException("Expected Double, but found ${args.value?.javaClass}")
        }
    }

    abstract fun callD(a: Prop<Double>): Prop<*>

}

class Sqrt : FunctionDouble("sqrt") {
    override fun callD(a: Prop<Double>): Prop<*> = DoubleSqrt(a)
}

abstract class Function2(name: String) : Function(name) {
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

abstract class FunctionDoubleDouble(name: String) : Function2(name) {
    override fun call(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return callDD(a as Prop<Double>, b as Prop<Double>)
        } else {
            throw RuntimeException("Expected arguments (Double,Double)")
        }
    }

    abstract fun callDD(a: Prop<Double>, b: Prop<Double>): Prop<*>
}

class NewVector2 : FunctionDoubleDouble("Vector2") {
    override fun callDD(a: Prop<Double>, b: Prop<Double>): Prop<*> {
        return LinkedVector2(a, b)
    }
}
