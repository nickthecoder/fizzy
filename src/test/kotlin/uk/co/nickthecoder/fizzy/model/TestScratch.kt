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
import uk.co.nickthecoder.fizzy.prop.DoubleExpression
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestScratch : MyTestCase() {

    @Test
    fun testScratch() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page)

        box.scratches.add(Scratch("foo", DoubleExpression("100")))
        box.scratches.add(Scratch("bar", DoubleExpression("10")))
        box.scratches.add(Scratch("ref1", DoubleExpression("this.findScratch(\"foo\")")))
        box.scratches.add(Scratch("ref2", DoubleExpression("this.findScratch(\"bar\")")))
        box.scratches.add(Scratch("ref3", DoubleExpression("Scratch.foo")))
        box.scratches.add(Scratch("ref4", DoubleExpression("Scratch.bar")))

        // Check the values directly
        assertEquals(100.0, box.scratches[0].expression.value)
        assertEquals(10.0, box.scratches[1].expression.value)

        // Check the values via a Scratch
        assertEquals(100.0, box.scratches[2].expression.value)
        assertEquals(10.0, box.scratches[3].expression.value)

        // Check via the newer (better, easier) syntax
        assertEquals(100.0, box.scratches[4].expression.value)
        assertEquals(10.0, box.scratches[5].expression.value)

        // Swap the names of foo and bar
        box.scratches[0].name.value = "bar"
        box.scratches[1].name.value = "foo"

        // Note, these are the same tests, with the 2 and 3 switched over.
        assertEquals(100.0, box.scratches[3].expression.value)
        assertEquals(10.0, box.scratches[2].expression.value)

        assertEquals(100.0, box.scratches[5].expression.value)
        assertEquals(10.0, box.scratches[4].expression.value)

        // Change the values
        box.scratches[0].expression.formula = "80"
        box.scratches[1].expression.formula = "60"

        // Note the indices are still switched!
        assertEquals(80.0, box.scratches[3].expression.value)
        assertEquals(60.0, box.scratches[2].expression.value)

        assertEquals(80.0, box.scratches[5].expression.value)
        assertEquals(60.0, box.scratches[4].expression.value)
    }
}
