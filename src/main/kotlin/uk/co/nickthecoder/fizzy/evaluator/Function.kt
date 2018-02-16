package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.DoubleSqrt
import uk.co.nickthecoder.fizzy.prop.Prop


abstract class Function(name: String) : Prop<String>(name) {
    abstract fun call(args: Prop<*>): Prop<*>

    companion object {
        val functions = mutableMapOf<String, Function>()

        init {
            add(Sqrt())
        }

        fun find(name: String) = functions.get(name)

        fun add(vararg funcs: Function) {
            funcs.forEach {
                functions.put(it.value, it)
            }
        }
    }
}

abstract class DoubleFunction(name: String) : Function(name) {

    override fun call(args: Prop<*>): Prop<*> {
        if (args.value is Double) {
            return callDouble(args as Prop<Double>)
        } else {
            throw RuntimeException("Expected Double, but found ${args.value?.javaClass}")
        }
    }

    abstract fun callDouble(a: Prop<Double>): Prop<*>

}

class Sqrt : DoubleFunction("sqrt") {
    override fun callDouble(a: Prop<Double>): Prop<*> = DoubleSqrt(a as Prop<Double>)
}
