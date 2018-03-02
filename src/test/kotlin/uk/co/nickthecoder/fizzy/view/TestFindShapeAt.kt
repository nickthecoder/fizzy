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
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.util.MyShapeTest
import uk.co.nickthecoder.fizzy.util.MyTestCase

/**
 * Tests [Page.findShapeAt] and also [Shape.isAt].
 */
class TestFindShapeAt : MyTestCase(), MyShapeTest {

    @Test
    fun testSimpleBox() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2(20mm,40mm)", "Dimension2(40mm, 120mm)")
        // Box from 30mm, 100m to 50mm, 140mm

        box.geometries[0].value.fill.formula = "false"

        assertTrue(box.isAt(dimension2("Dimension2(40mm,100mm)")))
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,100mm)")))
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"))) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"))) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"))) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"))) // Too high
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,120mm)"))) // Middle of shape (which isn't filled).

        box.geometries[0].value.fill.formula = "true"
        // Not that it is filled, the middle point should be be found.
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"))) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"))) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"))) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"))) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)"))) // Middle of shape (which is now filled).

        // An edge case. If we remove the last geometry part, making the left edge open, then the "Too Left" case is special
        box.geometries[0].value.parts.removeLast()
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"))) // Too left - This is special!
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"))) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"))) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"))) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)"))) // Middle of shape (which is now filled).
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
        // Box from 30mm, 100m to 50mm, 140mm

        box.geometries[0].value.fill.formula = "true"

        val geometry = box.geometries[0].value
        geometry.parts.clear()
        geometry.parts.add(MoveTo("Size * Vector2(1,1)"))
        geometry.parts.add(LineTo("Size * Vector2(0,1)"))
        geometry.parts.add(LineTo("Size * Vector2(0,0)"))
        geometry.parts.add(LineTo("Size * Vector2(1,0)"))
        geometry.parts.add(LineTo("Geometry1.Point1"))

        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"))) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"))) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"))) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"))) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)")))

        // Now open the right side of the box, and we should STILL find it.
        geometry.parts.removeLast()
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"))) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"))) // Too right - This is special
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,150mm)"))) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"))) // Too high
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,120mm)")))

        // I could test removing all four sides, but I know that the right side is the tricky edge case!
        // Also, removing the left edge is done in testSimpleBox.
    }
}
