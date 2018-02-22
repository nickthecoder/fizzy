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
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestVector2 : MyTestCase() {

    val zero = Vector2(0.0, 0.0)
    val unit = Vector2(1.0, 1.0)

    val a = Vector2(2.0, 1.0)
    val b = Vector2(0.5, 3.5)

    override fun tearDown() {
        // Make sure values haven't changed
        assertEquals(0.0, zero.x, tiny)
        assertEquals(0.0, zero.y, tiny)

        assertEquals(1.0, unit.y, tiny)
        assertEquals(1.0, unit.x, tiny)

        assertEquals(2.0, a.x, tiny)
        assertEquals(1.0, a.y, tiny)

        assertEquals(0.5, b.x, tiny)
        assertEquals(3.5, b.y, tiny)
    }

    @Test
    fun testValues() {
        assertEquals(0.0, zero.x, tiny)
        assertEquals(0.0, zero.y, tiny)

        assertEquals(1.0, unit.x, tiny)
        assertEquals(1.0, unit.y, tiny)

        assertEquals(2.0, a.x, tiny)
        assertEquals(1.0, a.y, tiny)
    }

    @Test
    fun testPlus() {

        // unit + zero
        val d = unit + zero
        assertEquals(1.0, d.x, tiny)
        assertEquals(1.0, d.y, tiny)

        // a + b
        val f = a + b
        assertEquals(2.5, f.x, tiny)
        assertEquals(4.5, f.y, tiny)

    }

    @Test
    fun testMinus() {
        // unit - zero
        val d = unit + zero
        assertEquals(1.0, d.x, tiny)
        assertEquals(1.0, d.y, tiny)

        // a - b
        val f = a - b
        assertEquals(1.5, f.x, tiny)
        assertEquals(-2.5, f.y, tiny)
    }

}
