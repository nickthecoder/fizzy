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
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.controller.MockOtherActions
import uk.co.nickthecoder.fizzy.controller.tools.EditGeometryTool
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestEditGeometry : MyTestCase() {

    @Test
    fun testSimple() {
        val doc = Document()
        val page = Page(doc)
        val controller = Controller(page, otherActions = MockOtherActions())

        val box = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(0mm,0mm)")
        controller.selection.add(box)

        assertEquals(10.0, box.geometry.parts[1].point.value.x.mm)
        assertEquals(10.0, box.geometry.parts[2].point.value.x.mm)

        controller.tool = EditGeometryTool(controller)

        // Drag the 3rd part to the center of the shape (we should have a triangle).
        drag(controller, "Dimension2(5mm,5mm)", "Dimension2(0mm,0mm)")

        assertEquals(5.0, box.geometry.parts[2].point.value.x.mm)
        assertEquals(10.0, box.size.value.x.mm) // Still the same size
        assertEquals(5.0, box.transform.locPin.value.x.mm) // Still at 5,5

        // Drag the 2nd part half way along. The size.x should be halved now.
        drag(controller, "Dimension2(5mm,-5mm)", "Dimension2(0mm,-5mm)")

        assertEquals(5.0, box.geometry.parts[1].point.value.x.mm)
        assertEquals(5.0, box.size.value.x.mm)
        assertEquals(5.0, box.transform.locPin.value.x.mm) // Still at 5,5

    }

    /**
     */
    @Test
    fun testUnjoin() {
        val doc = Document()
        val page = Page(doc)
        val controller = Controller(page, otherActions = MockOtherActions())

        val box = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(0mm,0mm)")
        controller.selection.add(box)

        val first = box.geometry.parts[0].point
        val last = box.geometry.parts[4].point
        assertEquals(0.0, first.value.x.mm)
        assertEquals(0.0, first.value.y.mm)
        assertEquals(0.0, last.value.x.mm)
        assertEquals(0.0, last.value.y.mm)

        val initialMetaData = box.metaData().toString()
        controller.tool = EditGeometryTool(controller)

        // Drag to the center of the shape (we should have a triangle).
        // We will be dragging the "MoveTo", which will cause the last to change too (as it is linked).
        drag(controller, "Dimension2(-5mm,-5mm)", "Dimension2(0mm,0mm)")

        val postDrag1MetaData = box.metaData().toString()

        assertEquals(5.0, first.value.x.mm)
        assertEquals(5.0, first.value.y.mm)
        assertEquals(5.0, last.value.x.mm)
        assertEquals(5.0, last.value.y.mm)

        // Drag to the edge,
        // However, now the last is NOT linked, so dragging again will move them away from each other.
        drag(controller, "Dimension2(0mm,0mm)", "Dimension2(-5mm,0mm)")

        assertEquals(0.0, first.value.x.mm)
        assertEquals(5.0, first.value.y.mm)
        assertEquals(5.0, last.value.x.mm)
        assertEquals(5.0, last.value.y.mm)

        // Undo twice, and we should be back to the start
        doc.history.undo()

        assertEquals(postDrag1MetaData, box.metaData().toString())

        assertEquals(5.0, first.value.x.mm)
        assertEquals(5.0, first.value.y.mm)
        assertEquals(5.0, last.value.x.mm)
        assertEquals(5.0, last.value.y.mm)

        doc.history.undo()

        assertEquals(initialMetaData, box.metaData().toString())

        assertEquals(0.0, first.value.x.mm)
        assertEquals(0.0, first.value.y.mm)
        assertEquals(0.0, last.value.x.mm) // This line used to fail ;-)
        assertEquals(0.0, last.value.y.mm)

    }


}
