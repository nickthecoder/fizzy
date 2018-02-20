package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.ArgList
import kotlin.reflect.KClass

abstract class PropMethod<T : Any, R : Any>(val prop: Prop<T>)
    : PropCalculation<R>() {

    init {
        prop.listeners.add(this)
    }

    protected var arg: Prop<*>? = null

    fun applyArgs(arg: Prop<*>) {
        this.arg = arg
        if (arg is ArgList) {
            arg.value.forEach { single ->
                if (!single.isConstant()) {
                    single.listeners.add(this)
                }
            }
        } else {
            arg.listeners.add(this)
        }
    }

    override fun eval(): R {
        arg?.let {
            return eval(it)
        }
        throw RuntimeException("Arguments not supplied")
    }

    abstract fun eval(arg: Prop<*>): R

    override fun toString(): String {
        if (arg == null) {
            return "Method (arguments not supplied yet)"
        }
        return "Method value=${safeValue()}"
    }
}

class PropMethod0<T : Any, R : Any>(prop: Prop<T>, val lambda: () -> R)
    : PropMethod<T, R>(prop) {

    override fun eval(arg: Prop<*>): R {
        if (arg is ArgList && arg.value.size == 0) {
            return lambda()
        }
        throw RuntimeException("Expected no arguments, but found $arg")
    }
}

open class PropMethod1<T : Any, A : Any, R : Any>(prop: Prop<T>, val klassA: KClass<A>, val lambda: (A) -> R)
    : PropMethod<T, R>(prop) {

    override fun eval(arg: Prop<*>): R {
        if (klassA.isInstance(arg.value)) {
            @Suppress("UNCHECKED_CAST")
            return lambda(arg.value as A)
        }
        throw RuntimeException("Expected an argument of type ${klassA.simpleName}, but found $arg")
    }
}

open class PropMethod2<T : Any, A : Any, B : Any, R : Any>(prop: Prop<T>, val klassA: KClass<A>, val klassB: KClass<B>, val lambda: (A, B) -> R)
    : PropMethod<T, R>(prop) {

    override fun eval(arg: Prop<*>): R {
        if (arg is ArgList && arg.value.size == 2) {
            val a = arg.value[0]
            val b = arg.value[1]

            if (klassA.isInstance(a.value) && klassB.isInstance(b.value)) {
                @Suppress("UNCHECKED_CAST")
                return lambda(a.value as A, b.value as B)
            }

        }
        throw RuntimeException("Expected arguments (${klassA.simpleName}, ${klassB.simpleName}), but found $arg")
    }
}
