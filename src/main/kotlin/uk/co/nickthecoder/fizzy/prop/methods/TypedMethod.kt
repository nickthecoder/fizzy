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
package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.evaluator.ArgList
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropMethod
import kotlin.reflect.KClass

abstract class TypedMethod1<T : Any, A : Any>(
        prop: Prop<T>,
        val aClass: KClass<A>
)

    : PropMethod<T>(prop) {

    override fun eval(arg: Prop<*>): Any {
        if (arg !is ArgList && aClass.isInstance(arg.value)) {
            listenTo(prop)
            listenTo(arg)
            @Suppress("UNCHECKED_CAST")
            return eval(arg.value as A)
        }
        throw RuntimeException("Expected an argument of type (${aClass.simpleName}), but found $arg")
    }

    protected abstract fun eval(a: A): Any
}

abstract class TypedMethod2<T : Any, A : Any, B : Any>(
        prop: Prop<T>,
        val aClass: KClass<A>,
        val bClass: KClass<B>
)

    : PropMethod<T>(prop) {

    override fun eval(arg: Prop<*>): Any {
        if (arg is ArgList && arg.value.size == 2) {
            if (aClass.isInstance(arg.value[0].value) && bClass.isInstance(arg.value[1].value)) {
                listenTo(prop)
                listenTo(arg.value[0])
                listenTo(arg.value[1])
                @Suppress("UNCHECKED_CAST")
                return eval(arg.value[0].value as A, arg.value[1].value as B)
            }
        }
        throw RuntimeException("Expected arguments of type (${aClass.simpleName}, ${bClass.simpleName}), but found $arg")
    }

    protected abstract fun eval(a: A, b: B): Any
}
