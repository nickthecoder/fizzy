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
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestDimension2 : MyTestCase() {

    @Test
    fun testCreate() {

        val a = Evaluator("Dimension2(1 mm, 2 mm)").parse() as Prop<Dimension2>
        assertEquals(1.0, a.value.x.mm, tiny)
        assertEquals(2.0, a.value.y.mm, tiny)
    }

    @Test
    fun testIsConstant() {

        val a = Evaluator("Dimension2(1m,2m)").parse()
        assert(a.isConstant()) { "$a" }
        assertEquals(true, a.isConstant())

        val b1 = Evaluator("Dimension2(1m,2m).X").parse()
        assert(b1.isConstant()) { "$b1" }
        val b2 = Evaluator("Dimension2(1m,2m).Y").parse()
        assert(b2.isConstant()) { "$b2" }

        val c1 = Evaluator("Dimension2(1m + 1m,2m)").parse()
        assert(!c1.isConstant()) { "$c1" }
        val c2 = Evaluator("Dimension2(1m,2m+1m)").parse()
        assert(!c2.isConstant()) { "$c2" }
        val c3 = Evaluator("Dimension2(1m + 1m,2m).X").parse()
        assert(!c3.isConstant()) { "$c3" }
        val c4 = Evaluator("Dimension2(1m + 1m,2m).Y").parse()
        assert(!c4.isConstant()) { "$c4" }


        val d = Evaluator("Dimension2(1m,1m).Angle").parse()
        assert(d.isConstant()) { "$d" }

        val e = Evaluator("Dimension2(1m,1m).Length").parse()
        assert(e.isConstant()) { "$e" }

        val f = Evaluator("Dimension2(1m,1m).normalise()").parse()
        assert(f.isConstant()) { "$f" }
    }

    @Test
    fun testMaths() {

        val a = Evaluator("Dimension2(1mm ,2mm) * 2").parse() as Prop<Dimension2>
        assertEquals(2.0, a.value.x.mm, tiny)
        assertEquals(4.0, a.value.y.mm, tiny)

        val b = Evaluator("Dimension2(8mm,10mm) / 2").parse() as Prop<Dimension2>
        assertEquals(4.0, b.value.x.mm, tiny)
        assertEquals(5.0, b.value.y.mm, tiny)

        assertFailsAt(14) {
            Evaluator("5 / Dimension2(1,1)").parse()
        }

        val c = Evaluator("10 * Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(80.0, c.value.x.mm, tiny)
        assertEquals(100.0, c.value.y.mm, tiny)

        val d = Evaluator("Dimension2(1mm,2mm) + Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(9.0, d.value.x.mm, tiny)
        assertEquals(12.0, d.value.y.mm, tiny)

        val e = Evaluator("Dimension2(1mm,2mm) - Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(-7.0, e.value.x.mm, tiny)
        assertEquals(-8.0, e.value.y.mm, tiny)

        val f = Evaluator("Dimension2(3mm,2mm) * Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(24.0, f.value.x.mm, tiny)
        assertEquals(20.0, f.value.y.mm, tiny)

        val g = Evaluator("-(Dimension2(3mm,2mm))").parse() as Prop<Dimension2>
        assertEquals(-3.0, g.value.x.mm, tiny)
        assertEquals(-2.0, g.value.y.mm, tiny)

        val h = Evaluator("-Dimension2(3mm,2mm)").parse() as Prop<Dimension2>
        assertEquals(-3.0, h.value.x.mm, tiny)
        assertEquals(-2.0, h.value.y.mm, tiny)

        val i = Evaluator("Dimension2(6mm,2mm) % Dimension2(3mm, 2mm)").parse() as Prop<Vector2>
        assertEquals(2.0, i.value.x, tiny)
        assertEquals(1.0, i.value.y, tiny)
        val i3 = Evaluator("Dimension2(6m,2m) / Dimension2(3m, 2m)").parse() as Prop<Dimension2>
        assertEquals(2.0, i3.value.x.inDefaultUnits, tiny)
        assertEquals(1.0, i3.value.y.inDefaultUnits, tiny)
        assertEquals(0.0, i3.value.x.power, tiny)
        assertEquals(0.0, i3.value.y.power, tiny)

        val j = Evaluator("Dimension2(6m,2m) / 2").parse() as Prop<Dimension2>
        assertEquals(3.0, j.value.x.m, tiny)
        assertEquals(1.0, j.value.y.m, tiny)
    }

    @Test
    fun testIllegalOperations() {

        assertFailsAt(2) {
            Evaluator("5 / Dimension2(1mm,1mm)").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 + Dimension2(1mm,1mm)").parse()
        }
        assertFailsAt(20) {
            Evaluator("Dimension2(1mm,1mm) + 5").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 - Dimension2(1mm,1mm)").parse()
        }
        assertFailsAt(20) {
            Evaluator("Dimension2(1mm,1mm) - 5").parse()
        }

    }

    @Test
    fun testWithVector2() {
        val a = Evaluator("Dimension2(3mm,2mm) * Vector2(2, 5)").parse() as Prop<Dimension2>
        assertEquals(6.0, a.value.x.mm, tiny)
        assertEquals(10.0, a.value.y.mm, tiny)

        val b = Evaluator("Vector2(5, 2) * Dimension2(3mm,2mm)").parse() as Prop<Dimension2>
        assertEquals(15.0, b.value.x.mm, tiny)
        assertEquals(4.0, b.value.y.mm, tiny)

        val c = Evaluator("Dimension2(6mm,3mm) / Vector2(2, 3)").parse() as Prop<Dimension2>
        assertEquals(3.0, c.value.x.mm, tiny)
        assertEquals(1.0, c.value.y.mm, tiny)

        assertFailsAt(13) {
            Evaluator("Vector2(1,1) + Dimension2(1m,1m)").parse()
        }
        assertFailsAt(13) {
            Evaluator("Vector2(1,1) - Dimension2(1m,1m)").parse()
        }
    }

    @Test
    fun testFields() {
        val a = Evaluator("Dimension2(15mm,10cm).X").parse() as Prop<Dimension>
        assertEquals(15.0, a.value.mm, tiny)

        val b = Evaluator("Dimension2(15mm,10cm).Y").parse() as Prop<Dimension>
        assertEquals(100.0, b.value.mm, tiny)

        val c = Evaluator("Dimension2(15mm,10cm).Y.mm").parse() as Prop<Double>
        assertEquals(100.0, c.value, tiny)

        val d = Evaluator("Dimension2(3m,4m).Length").parse() as Prop<Dimension>
        assertEquals(5.0, d.value.m, tiny)

    }

    @Test
    fun testMethods() {
        val a = Evaluator("Dimension2(3m,4m).rotate(90 deg)").parse() as Prop<Dimension2>
        assertEquals(-4.0, a.value.x.m, tiny)
        assertEquals(3.0, a.value.y.m, tiny)

        val a2 = Evaluator("Dimension2(3m,4m).rotate(-90 deg)").parse() as Prop<Dimension2>
        assertEquals(4.0, a2.value.x.m, tiny)
        assertEquals(-3.0, a2.value.y.m, tiny)

        // The x and y units of the results should be from the source's x.
        val a3 = Evaluator("Dimension2(3m,400cm).rotate(-90 deg)").parse() as Prop<Dimension2>
        assertEquals(Dimension.Units.m, a3.value.x.units)
        assertEquals(Dimension.Units.m, a3.value.y.units)
        assertEquals(4.0, a2.value.x.m, tiny)
        assertEquals(-3.0, a2.value.y.m, tiny)

        val b = Evaluator("Dimension2(3m,4m).normalise()").parse() as Prop<Vector2>
        assertEquals(3.0 / 5.0, b.value.x, tiny)
        assertEquals(4.0 / 5.0, b.value.y, tiny)
    }

}