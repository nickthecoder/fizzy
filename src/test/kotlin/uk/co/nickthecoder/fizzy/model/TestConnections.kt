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

class TestConnections : MyTestCase() {

    @Test
    fun testConnectAlong() {
        val doc = Document()
        val page = Page(doc)
        val box1 = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(0mm,0mm)") // @ -5,-10 to 5, 10
        page.children.add(box1)
        val box2 = createBox(box1, "Dimension2(10mm,20mm)", "Dimension2(6mm,11mm)") // @ -4,-9 to 6, 11
        box1.children.add(box2)
        val line = createLine(page, "Dimension2(0mm,0mm)", "Dimension2(0mm,0mm)")
        page.children.add(line)

        // Note the "this." is optional, so I've left this one in, but removed the others.
        line.start.formula = "this.connectAlong(Page.Shape1.Geometry1, 0.025)" // 10th along 1st line
        assertEquals(-4.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)

        line.end.formula = "connectAlong(Page.Shape1.Geometry1, 0.525)" // 10th along 3rd line
        assertEquals(4.0, line.end.value.x.mm, tiny)
        assertEquals(10.0, line.end.value.y.mm, tiny)

        // Move move the box
        box1.transform.pin.formula = "Dimension2(20mm,0mm)" // Now @ 15,-10 to 25,10
        assertEquals(16.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)
        assertEquals(24.0, line.end.value.x.mm, tiny)
        assertEquals(10.0, line.end.value.y.mm, tiny)

        box1.transform.pin.formula = "Dimension2(0mm,0mm)" // Now @ -5,-10 to 5, 10

        // Connect to the INNER box
        line.start.formula = "connectAlong(Page.Shape2.Geometry1, 0.025)" // 10th along 1st line
        assertEquals(-3.0, line.start.value.x.mm, tiny)
        assertEquals(-9.0, line.start.value.y.mm, tiny)

        // Move the OUTER box, the connection should also move as this also moves the INNER box.
        box1.transform.pin.formula = "Dimension2(20mm,0mm)" // Now @ 15,-10 to 25,10
        assertEquals(17.0, line.start.value.x.mm, tiny)
        assertEquals(-9.0, line.start.value.y.mm, tiny)


    }

    @Test
    fun testConnectTo() {
        val doc = Document()
        val page = Page(doc)

        val box1 = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(0mm,0mm)") // @ -5,-10 to 5, 10
        val box2 = createBox(box1, "Dimension2(10mm,20mm)", "Dimension2(6mm,11mm)") // @ -4,-9 to 6, 11
        val line = createLine(page, "Dimension2(0mm,0mm)", "Dimension2(0mm,0mm)")
        page.children.add(box1)
        box1.children.add(box2)
        page.children.add(line)

        box1.addConnectionPoint(ConnectionPoint("(Geometry1.Point1 + Geometry1.Point2) / 2"))
        box1.addConnectionPoint(ConnectionPoint("(Geometry1.Point2 + Geometry1.Point3) / 2"))

        box2.addConnectionPoint(ConnectionPoint("(Geometry1.Point3 + Geometry1.Point4) / 2"))

        line.start.formula = "connectTo(Page.Shape1.ConnectionPoint1)"
        assertEquals(0.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)

        line.end.formula = "connectTo(Page.Shape1.ConnectionPoint2)"
        assertEquals(5.0, line.end.value.x.mm, tiny)
        assertEquals(0.0, line.end.value.y.mm, tiny)

        // Move the box
        box1.transform.pin.formula = "Dimension2(20mm,0mm)"
        assertEquals(20.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)
        assertEquals(25.0, line.end.value.x.mm, tiny)
        assertEquals(0.0, line.end.value.y.mm, tiny)

        box1.transform.pin.formula = "Dimension2(0mm,0mm)"

        // Connect to the INNER box
        line.start.formula = "connectTo(Page.Shape2.ConnectionPoint1)"
        assertEquals(1.0, line.start.value.x.mm, tiny)
        assertEquals(11.0, line.start.value.y.mm, tiny)

        // Move the OUTER box, the connection should also move as this also moves the INNER box.
        box1.transform.pin.formula = "Dimension2(5mm,2mm)"
        assertEquals(6.0, line.start.value.x.mm, tiny)
        assertEquals(13.0, line.start.value.y.mm, tiny)
    }

    @Test
    fun testFindNearestConnectionGeometry() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(0mm,0mm)") // @ -5,-10 to 5, 10
        val box2 = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(1mm,0mm)")
        val line = createLine(page, "Dimension2(0mm,0mm)", "Dimension2(0mm,0mm)")
        page.children.add(box)
        page.children.add(box2)
        page.children.add(line)

        // Only allow connections to box, not to box2
        box.geometries[0].connect.formula = "true"

        /*
        // Half way along the first line
        val triple1 = page.findNearestConnectionGeometry(dimension2("Dimension2(0mm,-10mm)"), line)
        assertNotNull(triple1)
        assertEquals(box.geometries[0].value, triple1!!.first)
        assertEquals(0.0, triple1!!.second, tiny)
        assertEquals(0.125, triple1!!.third, tiny)

        // Half way along the second line
        val triple2 = page.findNearestConnectionGeometry(dimension2("Dimension2(5mm,0mm)"), line)
        assertNotNull(triple2)
        assertEquals(box.geometries[0].value, triple2!!.first)
        assertEquals(0.0, triple2!!.second, tiny)
        assertEquals(0.375, triple2!!.third, tiny)

        // Half way along the third line
        val triple3 = page.findNearestConnectionGeometry(dimension2("Dimension2(0mm,10mm)"), line)
        assertNotNull(triple3)
        assertEquals(box.geometries[0].value, triple3!!.first)
        assertEquals(0.0, triple3!!.second, tiny)
        assertEquals(0.625, triple3!!.third, tiny)

        // Half way along the last line
        val triple4 = page.findNearestConnectionGeometry(dimension2("Dimension2(-5mm,0mm)"), line)
        assertNotNull(triple4)
        assertEquals(box.geometries[0].value, triple4!!.first)
        assertEquals(0.0, triple4!!.second, tiny)
        assertEquals(0.875, triple4!!.third, tiny)
        */

        fun testNear(point: String): Boolean {
            val d2 = dimension2("Dimension2($point)")
            val triple = page.findNearestConnectionGeometry(d2, line)
            if (triple == null) {
                return false
            } else {
                return triple.second < 3.0
            }
        }

        // The center is too far
        assertFalse(testNear("0mm,0mm"))

        // Now test that near hits work
        assertTrue(testNear("-4mm,0mm"))
        assertTrue(testNear("-6mm,0mm"))
        assertFalse(testNear("-10mm,0mm"))

        assertTrue(testNear("4mm,0mm"))
        assertTrue(testNear("6mm,0mm"))
        assertFalse(testNear("10mm,0mm"))

        assertTrue(testNear("0mm,9mm"))
        assertTrue(testNear("0mm,11mm"))
        assertFalse(testNear("0mm,15mm"))

        assertTrue(testNear("0mm,-9mm"))
        assertTrue(testNear("0mm,-11mm"))
        assertFalse(testNear("0mm,-15mm"))
    }
}
