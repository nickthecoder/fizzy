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
package uk.co.nickthecoder.fizzy.model

import org.junit.Test
import uk.co.nickthecoder.fizzy.util.MyShapeTest
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestScratch : MyTestCase(), MyShapeTest {

    @Test
    fun testScratch() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page)

        box.addScratch(Scratch("foo", "100"))
        box.addScratch(Scratch("bar", "10 mm"))
        box.addScratch(Scratch("baz", "90 deg"))
        box.addScratch(Scratch("ref1", "this.findScratch(\"foo\""))
        box.addScratch(Scratch("ref2", "this.findScratch(\"bar\""))
        box.addScratch(Scratch("ref3", "this.findScratch(\"baz\""))

        // Check the values directly
        assertEquals(100.0, box.scratches[0].value.expression.value)
        assertEquals(Dimension(10.0, Dimension.Units.mm), box.scratches[1].value.expression.value)
        assertEquals(Angle.degrees(90.0), box.scratches[2].value.expression.value)

        // Check the values via a Scratch
        assertEquals(100.0, box.scratches[3].value.expression.value)
        assertEquals(Dimension(10.0, Dimension.Units.mm), box.scratches[4].value.expression.value)
        assertEquals(Angle.degrees(90.0), box.scratches[5].value.expression.value)

        // Swap the names of foo and bar
        box.scratches[0].value.name.value = "bar"
        box.scratches[1].value.name.value = "foo"

        // Note, these are the same tests, with the 3 and 4 switched over.
        assertEquals(100.0, box.scratches[4].value.expression.value)
        assertEquals(Dimension(10.0, Dimension.Units.mm), box.scratches[3].value.expression.value)
        assertEquals(Angle.degrees(90.0), box.scratches[5].value.expression.value)

        // Change the values (and the types of the last two)
        box.scratches[0].value.expression.expression = "80"
        box.scratches[1].value.expression.expression = "60"
        box.scratches[2].value.expression.expression = "40"

        // Note the indices are still switched!
        assertEquals(80.0, box.scratches[4].value.expression.value)
        assertEquals(60.0, box.scratches[3].value.expression.value)
        assertEquals(40.0, box.scratches[5].value.expression.value)
    }
}
