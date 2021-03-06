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
import uk.co.nickthecoder.fizzy.model.Color
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2

/**
 * See [DummyPropType]
 */
class Dummy

/**
 * Functions are implemented by the same mechanism as methods, where the [Prop] that the methods refers to is of type
 * Prop<[Dummy]> (and is is just ignored!
 */
class DummyPropType private constructor()
    : PropType<Dummy>(Dummy::class.java) {

    override fun findField(prop: Prop<Dummy>, name: String): PropField<Dummy>? {
        return null
    }

    override fun findMethod(prop: Prop<Dummy>, name: String): PropMethod<Dummy>? {
        return when (name) {
            "abs" -> PropFunction1(Double::class.java) { Math.abs(it) }
            "ceil" -> PropFunction1(Double::class.java) { Math.ceil(it) }
            "floor" -> PropFunction1(Double::class.java) { Math.floor(it) }
            "exp" -> PropFunction1(Double::class.java) { Math.exp(it) }
            "ln" -> PropFunction1(Double::class.java) { Math.log(it) }
            "log" -> PropFunction1(Double::class.java) { Math.log10(it) }
            "sqrt" -> PropFunction1(Double::class.java) { Math.sqrt(it) }

            "if" -> IfFunction()

            "Vector2" -> PropFunction2(Double::class.java, Double::class.java) { x, y -> Vector2(x, y) }
            "Dimension2" -> PropFunction2(Dimension::class.java, Dimension::class.java) { x, y -> Dimension2(x, y) }

            "WebColor" -> PropFunction1(String::class.java) { Color.web(it) }
            "RGB" -> PropFunction3(Double::class.java, Double::class.java, Double::class.java) { r, g, b -> Color(clamp0_1(r), clamp0_1(g), clamp0_1(b)) }
            "RGBA" -> PropFunction4(Double::class.java, Double::class.java, Double::class.java, Double::class.java) { r, g, b, a -> Color(clamp0_1(r), clamp0_1(g), clamp0_1(b), clamp0_1(a)) }
            else -> null
        }
    }

    companion object {
        val instance = DummyPropType()
    }
}

fun clamp0_1(v: Double) = Math.min(1.0, Math.max(0.0, v))
fun clamp0_360(v: Double) = Math.min(1.0, Math.max(0.0, v))
/**
 * The one and only instance of a [Prop] of type [Dummy] used as the [PropMethod]'s value when the 'method' is really a
 * function (and applies to nothing).
 */
val dummyInstance = PropConstant(Dummy())

class PropFunction1<A : Any>(klassA: Class<A>, lambda: (A) -> Any)
    : PropMethod1<Dummy, A>(dummyInstance, klassA, lambda)

class PropFunction2<A : Any, B : Any>(klassA: Class<A>, klassB: Class<B>, lambda: (A, B) -> Any)
    : PropMethod2<Dummy, A, B>(dummyInstance, klassA, klassB, lambda)

class PropFunction3<A : Any, B : Any, C : Any>(klassA: Class<A>, klassB: Class<B>, klassC: Class<C>, lambda: (A, B, C) -> Any)
    : PropMethod3<Dummy, A, B, C>(dummyInstance, klassA, klassB, klassC, lambda)

class PropFunction4<A : Any, B : Any, C : Any, D : Any>(klassA: Class<A>, klassB: Class<B>, klassC: Class<C>, klassD: Class<D>, lambda: (A, B, C, D) -> Any)
    : PropMethod4<Dummy, A, B, C, D>(dummyInstance, klassA, klassB, klassC, klassD, lambda)

class IfFunction : PropMethod<Dummy>(dummyInstance) {

    override fun eval(arg: Prop<*>): Any {
        if (arg is ArgList && arg.value.size == 3) {
            val condition = arg.value[0].value
            if (condition is Boolean) {
                val a = arg.value[1]
                val b = arg.value[2]
                return if (condition) a.value else b.value
            }
        }
        throw RuntimeException("Expected (Boolean,Any,Any), but found ${arg.value}")
    }
}