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

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestDimension : MyTestCase() {

    val two_mm = Dimension(2.0, "mm")
    val three_mm = Dimension(3.0, "mm")

    val two_cm = Dimension(2.0, "cm")
    val three_cm = Dimension(3.0, "cm")

    @Test
    fun testMaths() {

        assertEquals(1.0, Dimension.Units.mm.scale, tiny) // This may change!

        val a = two_mm + three_mm
        assertEquals(1.0, a.power, tiny)
        assertEquals(5.0, a.inDefaultUnits, tiny) // This may change!
        assertEquals(5.0, a.mm, tiny)
        assertEquals(0.5, a.cm, tiny)
        assertEquals(0.005, a.m, tiny)
        assertEquals(0.000005, a.km, tiny)

        val b = two_mm - three_mm
        assertEquals(-1.0, b.mm, tiny)
        assertEquals(1.0, b.power, tiny)

        val c = two_cm + three_cm
        assertEquals(50.0, c.mm, tiny)
        assertEquals(5.0, c.cm, tiny)
        assertEquals(0.05, c.m, tiny)
        assertEquals(0.00005, c.km, tiny)
        assertEquals(1.0, b.power, tiny)

        val d = two_mm * three_mm
        assertEquals(6.0, d.mm, tiny)
        assertEquals(0.06, d.cm, tiny)
        assertEquals(2.0, d.power, tiny)

        val e = two_cm * three_cm
        assertEquals(2.0, e.power, tiny)
        assertEquals(6.0, e.cm, tiny)
        assertEquals(600.0, e.mm, tiny)

        val f = two_cm + three_mm
        assertEquals(2.3, f.cm, tiny)

        val g = three_mm / two_mm
        assertEquals(0.0, g.power, tiny)
        assertEquals(1.5, g.inDefaultUnits, tiny)
        assertEquals(1.5, g.cm, tiny)
        assertEquals(1.5, g.km, tiny)

        val h = three_cm / three_mm
        assertEquals(0.0, h.power, tiny)
        assertEquals(10.0, h.inDefaultUnits, tiny)
        assertEquals(10.0, h.cm, tiny)
        assertEquals(10.0, h.km, tiny)
    }
}
