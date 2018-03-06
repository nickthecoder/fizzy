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
import uk.co.nickthecoder.fizzy.prop.AngleExpression
import uk.co.nickthecoder.fizzy.prop.DoubleExpression
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestScratch : MyTestCase() {

    @Test
    fun testScratch() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page)

        box.addScratch(Scratch("foo", DoubleExpression("100")))
        box.addScratch(Scratch("bar", DoubleExpression("10")))
        box.addScratch(Scratch("baz", AngleExpression("90 deg")))
        box.addScratch(Scratch("ref1", DoubleExpression("this.findScratch(\"foo\"")))
        box.addScratch(Scratch("ref2", DoubleExpression("this.findScratch(\"bar\"")))
        box.addScratch(Scratch("ref3", AngleExpression("this.findScratch(\"baz\"")))

        // Check the values directly
        assertEquals(100.0, box.scratches[0].value.expression.value)
        assertEquals(10.0, box.scratches[1].value.expression.value)
        assertEquals(Angle.degrees(90.0), box.scratches[2].value.expression.value)

        // Check the values via a Scratch
        assertEquals(100.0, box.scratches[3].value.expression.value)
        assertEquals(10.0, box.scratches[4].value.expression.value)
        assertEquals(Angle.degrees(90.0), box.scratches[5].value.expression.value)

        // Swap the names of foo and bar
        box.scratches[0].value.name.value = "bar"
        box.scratches[1].value.name.value = "foo"

        // Note, these are the same tests, with the 3 and 4 switched over.
        assertEquals(100.0, box.scratches[4].value.expression.value)
        assertEquals(10.0, box.scratches[3].value.expression.value)
        assertEquals(Angle.degrees(90.0), box.scratches[5].value.expression.value)

        // Change the values
        box.scratches[0].value.expression.formula = "80"
        box.scratches[1].value.expression.formula = "60"
        box.scratches[2].value.expression.formula = "40 deg"

        // Note the indices are still switched!
        assertEquals(80.0, box.scratches[4].value.expression.value)
        assertEquals(60.0, box.scratches[3].value.expression.value)
        assertEquals(Angle.degrees(40.0), box.scratches[5].value.expression.value)
    }
}
