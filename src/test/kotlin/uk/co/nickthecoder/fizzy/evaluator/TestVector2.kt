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
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestVector2 : MyTestCase() {

    @Test
    fun testCreate() {

        val a = Evaluator("Vector2(1,2)").parse() as Prop<Vector2>
        assertEquals(1.0, a.value.x, tiny)
        assertEquals(2.0, a.value.y, tiny)
    }

    @Test
    fun testMaths() {
        val a = Evaluator("Vector2(1,2) * 2").parse() as Prop<Vector2>
        assertEquals(2.0, a.value.x, tiny)
        assertEquals(4.0, a.value.y, tiny)

        val b = Evaluator("Vector2(8,10) / 2").parse() as Prop<Vector2>
        assertEquals(4.0, b.value.x, tiny)
        assertEquals(5.0, b.value.y, tiny)

        val c = Evaluator("10 * Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(80.0, c.value.x, tiny)
        assertEquals(100.0, c.value.y, tiny)

        assertFailsAt(2) {
            Evaluator("5 / Vector2(1,1)").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 + Vector2(1,1)").parse()
        }
        assertFailsAt(13) {
            Evaluator("Vector2(1,1) + 5").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 - Vector2(1,1)").parse()
        }
        assertFailsAt(13) {
            Evaluator("Vector2(1,1) - 5").parse()
        }

        val d = Evaluator("Vector2(1,2) + Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(9.0, d.value.x, tiny)
        assertEquals(12.0, d.value.y, tiny)

        val e = Evaluator("Vector2(1,2) - Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(-7.0, e.value.x, tiny)
        assertEquals(-8.0, e.value.y, tiny)

        val f = Evaluator("Vector2(3,2) * Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(24.0, f.value.x, tiny)
        assertEquals(20.0, f.value.y, tiny)

        val g = Evaluator("-Vector2(3,2)").parse() as Prop<Vector2>
        assertEquals(-3.0, g.value.x, tiny)
        assertEquals(-2.0, g.value.y, tiny)

    }

    @Test
    fun testIsConstant() {

        val a = Evaluator("Vector2(1,2)").parse()
        assert(a.isConstant()) { "$a" }
        val b1 = Evaluator("Vector2(1,2).X").parse()
        assert(b1.isConstant()) { "$b1" }
        val b2 = Evaluator("Vector2(1,2).Y").parse()
        assert(b2.isConstant()) { "$b2" }

        val c1 = Evaluator("Vector2(1 + 1,2)").parse()
        assert(!c1.isConstant()) { "$c1" }
        val c2 = Evaluator("Vector2(1,2+1)").parse()
        assert(!c2.isConstant()) { "$c2" }
        val c3 = Evaluator("Vector2(1 + 1,2).X").parse()
        assert(!c3.isConstant()) { "$c3" }
        val c4 = Evaluator("Vector2(1 + 1,2).Y").parse()
        assert(!c4.isConstant()) { "$c4" }

        val d = Evaluator("Vector2(1,1).Angle").parse()
        assert(d.isConstant()) { "$d" }

        val e = Evaluator("Vector2(1,1).Length").parse()
        assert(e.isConstant()) { "$e" }

        val f = Evaluator("Vector2(1,1).normalise()").parse()
        assert(f.isConstant()) { "$f" }

    }

    @Test
    fun testFields() {
        val a = Evaluator("Vector2(15,10).X").parse() as Prop<Double>
        assertEquals(15.0, a.value, tiny)

        val b = Evaluator("Vector2(15,10).Y").parse() as Prop<Double>
        assertEquals(10.0, b.value, tiny)
    }

    @Test
    fun testMethods() {
        val a = Evaluator("Vector2(3,4).Length").parse() as Prop<Double>
        assertEquals(5.0, a.value, tiny)

        val b = Evaluator("Vector2(3,4).normalise()").parse() as Prop<Vector2>
        assertEquals(3.0 / 5.0, b.value.x, tiny)
        assertEquals(4.0 / 5.0, b.value.y, tiny)

        val c = Evaluator("Vector2(3,4).rotate(90 deg)").parse() as Prop<Vector2>
        assertEquals(-4.0, c.value.x, tiny)
        assertEquals(3.0, c.value.y, tiny)

        val d = Evaluator("Vector2(3,4).rotate(-90 deg)").parse() as Prop<Vector2>
        assertEquals(4.0, d.value.x, tiny)
        assertEquals(-3.0, d.value.y, tiny)

    }
}