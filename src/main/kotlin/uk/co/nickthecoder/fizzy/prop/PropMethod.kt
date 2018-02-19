package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.ArgList

class PropMethod<T : Any, F : Any>(val prop: Prop<T>, val lambda: (Prop<T>, ArgList) -> F)
    : PropCalculation<F>() {

    private var args: ArgList? = null

    fun setArgs(args: ArgList) {
        if (this.args != null) {
            throw RuntimeException("Arguments already set")
        }
        this.args = args
        args.value.forEach { arg ->
            if (!arg.isConstant()) {
                args.listeners.add(this)
            }
        }
    }

    override fun eval(): F {
        args?.let {
            return lambda(prop, it)
        }
        throw RuntimeException("Arguments to ${this} not supplied")
    }

    override fun toString(): String {
        return "Method value=${safeValue()}"
    }
}
