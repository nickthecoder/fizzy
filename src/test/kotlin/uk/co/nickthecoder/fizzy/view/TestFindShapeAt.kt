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
package uk.co.nickthecoder.fizzy.view

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.util.MyTestCase

/**
 * Tests [Page.findShapeAt] and also [Shape.isAt].
 */
class TestFindShapeAt : MyTestCase() {

    @Test
    fun testSimpleBox() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2(20mm,40mm)", "Dimension2(40mm, 120mm)")
        // Box from 30mm, 100m to 50mm, 140mm
        page.children.add(box)

        box.geometry.fill.formula = "false"

        assertTrue(box.isAt(dimension2("Dimension2(40mm,100mm)"), Dimension.ZERO_mm))
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,100mm)"), Dimension.ZERO_mm))
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"), Dimension.ZERO_mm)) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"), Dimension.ZERO_mm)) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"), Dimension.ZERO_mm)) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"), Dimension.ZERO_mm)) // Too high
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,120mm)"), Dimension.ZERO_mm)) // Middle of shape (which isn't filled).

        box.geometry.fill.formula = "true"
        // Not that it is filled, the middle point should be be found.
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"), Dimension.ZERO_mm)) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"), Dimension.ZERO_mm)) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"), Dimension.ZERO_mm)) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"), Dimension.ZERO_mm)) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)"), Dimension.ZERO_mm)) // Middle of shape (which is now filled).

        // An edge case. If we remove the last geometry part, making the left edge open, then the "Too Left" case is special
        box.geometry.parts.removeLast()
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"), Dimension.ZERO_mm)) // Too left - This is special!
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"), Dimension.ZERO_mm)) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"), Dimension.ZERO_mm)) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"), Dimension.ZERO_mm)) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)"), Dimension.ZERO_mm)) // Middle of shape (which is now filled).
    }

    @Test
    fun testThinLine() {
        val doc = Document()
        val page = Page(doc)
        val line = createLine(page, "Dimension2(20mm,40mm)", "Dimension2(40mm, 40mm)", "0.1mm")
        page.children.add(line)

        assertEquals(line, page.findShapeAt(dimension2("Dimension2(30mm,40mm)"), Dimension.ZERO_mm))
        // We need to select the thin like by clicking CLOSE to it.
        assertEquals(null, page.findShapeAt(dimension2("Dimension2(30mm,41mm)"), Dimension(0.0, Dimension.Units.mm)))
        assertEquals(line, page.findShapeAt(dimension2("Dimension2(30mm,41mm)"), Dimension(1.1, Dimension.Units.mm)))

    }

    /**
     * Here we test that an open shape still works i.e. where the end point it not the start point.
     *
     */
    @Test
    fun testOpenShape() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2(20mm,40mm)", "Dimension2(40mm, 120mm)")
        page.children.add(box)

        // Box from 30mm, 100m to 50mm, 140mm

        box.geometry.fill.formula = "true"

        val geometry = box.geometry
        geometry.parts.clear()
        geometry.parts.add(MoveTo("Size * Vector2(1,1)"))
        geometry.parts.add(LineTo("Size * Vector2(0,1)"))
        geometry.parts.add(LineTo("Size * Vector2(0,0)"))
        geometry.parts.add(LineTo("Size * Vector2(1,0)"))
        geometry.parts.add(LineTo("Geometry1.Point1"))

        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"), Dimension.ZERO_mm)) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"), Dimension.ZERO_mm)) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"), Dimension.ZERO_mm)) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"), Dimension.ZERO_mm)) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)"), Dimension.ZERO_mm))

        // Now open the right side of the box, and we should STILL find it.
        geometry.parts.removeLast()
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"), Dimension.ZERO_mm)) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"), Dimension.ZERO_mm)) // Too right - This is special
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"), Dimension.ZERO_mm)) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"), Dimension.ZERO_mm)) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)"), Dimension.ZERO_mm))

        // I could test removing all four sides, but I know that the right side is the tricky edge case!
        // Also, removing the left edge is done in testSimpleBox.
    }
}
