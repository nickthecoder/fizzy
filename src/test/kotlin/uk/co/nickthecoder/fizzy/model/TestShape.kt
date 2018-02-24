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
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestShape : MyTestCase() {

    val document = Document()

    val layer1 = Page(document)

    val line = Shape1d(layer1)

    fun createBox(): Shape2d {
        val box = Shape2d(layer1)
        // Make a box 60mm x 40mm. centered at (40mm,120mm)
        box.size.expression = "Dimension2(60mm,120mm)"
        box.transform.pin.expression = "Dimension2(40mmx60mm)"
        val geometry = Geometry()
        box.geometries.add(geometry)
        // Make a box
        geometry.parts.add(MoveTo("Size * -0.5"))
        geometry.parts.add(LineTo("Size * Vector2(0.5,-0.5)"))
        geometry.parts.add(LineTo("Size * 0.5"))
        geometry.parts.add(LineTo("Size * Vector2(-0.5, 0.5)"))
        geometry.parts.add(LineTo("Geometry1.Point1"))

        return box
    }

    @Test
    fun testGeometry() {

        val box = createBox()
        val geometry = box.geometries[0]

        // The top left should be (-30,-60)
        assertEquals(-30.0, (geometry.parts[0] as MoveTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[0] as MoveTo).point.value.y.mm)

        // Which is (10mm, 30mm) within the document
        // TODO MORE test this!

        // The bottom right at (30mm,60mm)
        assertEquals(30.0, (geometry.parts[2] as LineTo).point.value.x.mm)
        assertEquals(60.0, (geometry.parts[2] as LineTo).point.value.y.mm)

        // Which is (50mm, 150mm) within the document
        // TODO Test this!

        // The last item, is the same as the first (i.e. it is closed).
        assertEquals(-30.0, (geometry.parts[4] as LineTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[4] as LineTo).point.value.y.mm)

        // For completeness, check the other two points (top right, then bottom left).
        assertEquals(30.0, (geometry.parts[1] as LineTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[1] as LineTo).point.value.y.mm)
        assertEquals(-30.0, (geometry.parts[3] as LineTo).point.value.x.mm)
        assertEquals(60.0, (geometry.parts[3] as LineTo).point.value.y.mm)

        // Now move the top left corner inwards
        val topLeft = geometry.parts[0] as MoveTo
        topLeft.point.expression = "Size * Vector2(0, -0.5)"
        assertEquals(0.0, (geometry.parts[0] as MoveTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[0] as MoveTo).point.value.y.mm) // Unchanged
        assertEquals(0.0, (geometry.parts[4] as LineTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[4] as LineTo).point.value.y.mm) // Unchanged

    }
}
