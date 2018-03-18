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

class TestUserData : MyTestCase() {

    @Test
    fun testUserData() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page)

        box.userDataList.add(UserData("foo", "Foo Label", "Foo"))
        box.userDataList.add(UserData("bar", "Bar Label", "Bar"))

        box.scratches.add(Scratch("ref1", DoubleExpression("UserData.foo")))
        box.scratches.add(Scratch("ref2", DoubleExpression("UserData.bar")))

        // Check the values directly
        assertEquals("Foo", box.userDataList[0].data.value)
        assertEquals("Bar", box.userDataList[1].data.value)

        // Check the values via a Scratch
        assertEquals("Foo", box.scratches[0].expression.value)
        assertEquals("Bar", box.scratches[1].expression.value)

        // Swap the names of foo and bar
        box.userDataList[0].name.value = "bar"
        box.userDataList[1].name.value = "foo"

        // Note, these are the same tests, with the 0 and 1 switched over.
        assertEquals("Foo", box.scratches[1].expression.value)
        assertEquals("Bar", box.scratches[0].expression.value)

        // Change the values
        box.userDataList[0].data.value = "Changed 0"
        box.userDataList[1].data.value = "Changed 1"

        // Note the indices are still switched!
        assertEquals("Changed 0", box.scratches[1].expression.value)
        assertEquals("Changed 1", box.scratches[0].expression.value)

    }
}
