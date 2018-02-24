/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.ArgList
import kotlin.reflect.KClass

/**
 * T is the type of the receiver object.
 */
abstract class PropMethod<T : Any>(val prop: Prop<T>)
    : PropCalculation<Any>() {


    protected var arg: Prop<*>? = null

    init {
        if (!prop.isConstant()) {
            prop.listeners.add(this)
        }
    }

    override fun isConstant() = prop.isConstant() && arg?.isConstant() == true

    fun applyArgs(arg: Prop<*>) {
        this.arg = arg
        if (arg is ArgList) {
            arg.value.forEach { single ->
                if (!single.isConstant()) {
                    single.listeners.add(this)
                }
            }
        } else {
            if (!arg.isConstant()) {
                arg.listeners.add(this)
            }
        }
    }

    override fun eval(): Any {
        arg?.let {
            return eval(it)
        }
        throw RuntimeException("Arguments not supplied")
    }

    abstract fun eval(arg: Prop<*>): Any
}

/**
 * A method, which has no arguments.
 * T is the type of the receiver object.
 */
class PropMethod0<T : Any>(
        prop: Prop<T>,
        val lambda: () -> Any)

    : PropMethod<T>(prop) {

    override fun eval(arg: Prop<*>): Any {
        if (arg is ArgList && arg.value.size == 0) {
            return lambda()
        }
        throw RuntimeException("Expected no arguments, but found $arg")
    }
}

/**
 * A method which has 1 argument.
 * T is the type of the receiver object.
 * A is the type of the method's argument.
 */
open class PropMethod1<T : Any, A : Any>(
        prop: Prop<T>, val klassA: KClass<A>,
        val lambda: (A) -> Any)

    : PropMethod<T>(prop) {

    override fun eval(arg: Prop<*>): Any {

        if (klassA.isInstance(arg.value)) {
            @Suppress("UNCHECKED_CAST")
            return lambda(arg.value as A)
        }
        throw RuntimeException("Expected an argument of type ${klassA.simpleName}, but found $arg")
    }
}

/**
 * A method which has 2 arguments.
 * T is the type of the receiver object.
 * A and B are the types of the method's arguments.
 */
open class PropMethod2<T : Any, A : Any, B : Any>(
        prop: Prop<T>, val klassA: KClass<A>,
        val klassB: KClass<B>,
        val lambda: (A, B) -> Any)

    : PropMethod<T>(prop) {

    override fun eval(arg: Prop<*>): Any {
        if (arg is ArgList && arg.value.size == 2) {
            val a = arg.value[0]
            val b = arg.value[1]

            if (klassA.isInstance(a.value) && klassB.isInstance(b.value)) {
                @Suppress("UNCHECKED_CAST")
                val result = lambda(a.value as A, b.value as B)

                return result
            }

        }
        throw RuntimeException("Expected arguments (${klassA.simpleName}, ${klassB.simpleName}), but found $arg")
    }
}
