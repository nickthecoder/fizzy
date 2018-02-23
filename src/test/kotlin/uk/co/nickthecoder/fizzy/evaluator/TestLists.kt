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
package uk.co.nickthecoder.fizzy.evaluator

import org.junit.Test
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropMethod
import uk.co.nickthecoder.fizzy.prop.PropType
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestLists : MyTestCase() {

    private class Foo(val angles: FList<Angle>)

    private class FooPropType : PropType<Foo>(Foo::class) {

        override fun findField(prop: Prop<Foo>, name: String): Prop<*>? {
            if (name == "angles") {
                return PropConstant(prop.value.angles)
            }
            return super.findField(prop, name)
        }

        override fun findMethod(prop: Prop<Foo>, name: String): PropMethod<Foo, *>? = null
    }

    @Test
    fun testLists() {
        PropType.put(FooPropType())
        val foo = Foo(FList(listOf(Angle.degrees(45.0), Angle.degrees(90.0))))
        val context = SimpleEvaluationContext(mapOf("foo" to PropConstant(foo)))

        val a = Evaluator("foo.angles1.Degrees", context).parse() as Prop<Double>
        assertEquals(45.0, a.value, tiny)

        val b = Evaluator("foo.angles2.Degrees", context).parse() as Prop<Double>
        assertEquals(90.0, b.value, tiny)

        assertFailsAt(3) {
            Evaluator("foo.angles0.Degrees", context).parse()
        }
        assertFailsAt(3) {
            Evaluator("foo.angles3.Degrees", context).parse()
        }

    }

}