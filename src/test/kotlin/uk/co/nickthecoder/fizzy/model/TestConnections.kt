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

import javafx.scene.paint.Color
import org.junit.Test
import uk.co.nickthecoder.fizzy.util.MyShapeTest
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestConnections : MyTestCase(), MyShapeTest {

    @Test
    fun testConnectAlong() {
        val doc = Document()
        val page = Page(doc)
val c = Color.BLACK
        val box1 = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(0mm,0mm)") // @ -5,-10 to 5, 10
        createBox(box1, "Dimension2(10mm,20mm)", "Dimension2(6mm,11mm)") // @ -4,-9 to 6, 11
        val line = createLine(page, "Dimension2(0mm,0mm)", "Dimension2(0mm,0mm)")

        // Note the "this." is optional, so I've left this one in, but removed the others.
        line.start.expression = "this.connectAlong(Page.Shape1.Geometry1, 0.025)" // 10th along 1st line
        assertEquals(-4.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)

        line.end.expression = "connectAlong(Page.Shape1.Geometry1, 0.525)" // 10th along 3rd line
        assertEquals(4.0, line.end.value.x.mm, tiny)
        assertEquals(10.0, line.end.value.y.mm, tiny)

        // Move move the box
        box1.transform.pin.expression = "Dimension2(20mm,0mm)" // Now @ 15,-10 to 25,10
        assertEquals(16.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)
        assertEquals(24.0, line.end.value.x.mm, tiny)
        assertEquals(10.0, line.end.value.y.mm, tiny)

        box1.transform.pin.expression = "Dimension2(0mm,0mm)" // Now @ -5,-10 to 5, 10

        // Connect to the INNER box
        line.start.expression = "connectAlong(Page.Shape2.Geometry1, 0.025)" // 10th along 1st line
        assertEquals(-3.0, line.start.value.x.mm, tiny)
        assertEquals(-9.0, line.start.value.y.mm, tiny)

        // Move the OUTER box, the connection should also move as this also moves the INNER box.
        box1.transform.pin.expression = "Dimension2(20mm,0mm)" // Now @ 15,-10 to 25,10
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

        box1.addConnectionPoint(ConnectionPoint("(Geometry1.Point1 + Geometry1.Point2) / 2", "0deg"))
        box1.addConnectionPoint(ConnectionPoint("(Geometry1.Point2 + Geometry1.Point3) / 2", "0deg"))

        box2.addConnectionPoint(ConnectionPoint("(Geometry1.Point3 + Geometry1.Point4) / 2", "0deg"))

        line.start.expression = "connectTo(Page.Shape1.ConnectionPoint1)"
        assertEquals(0.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)

        line.end.expression = "connectTo(Page.Shape1.ConnectionPoint2)"
        assertEquals(5.0, line.end.value.x.mm, tiny)
        assertEquals(0.0, line.end.value.y.mm, tiny)

        // Move the box
        box1.transform.pin.expression = "Dimension2(20mm,0mm)"
        assertEquals(20.0, line.start.value.x.mm, tiny)
        assertEquals(-10.0, line.start.value.y.mm, tiny)
        assertEquals(25.0, line.end.value.x.mm, tiny)
        assertEquals(0.0, line.end.value.y.mm, tiny)

        box1.transform.pin.expression = "Dimension2(0mm,0mm)"

        // Connect to the INNER box
        line.start.expression = "connectTo(Page.Shape2.ConnectionPoint1)"
        assertEquals(1.0, line.start.value.x.mm, tiny)
        assertEquals(11.0, line.start.value.y.mm, tiny)

        // Move the OUTER box, the connection should also move as this also moves the INNER box.
        box1.transform.pin.expression = "Dimension2(5mm,2mm)"
        assertEquals(6.0, line.start.value.x.mm, tiny)
        assertEquals(13.0, line.start.value.y.mm, tiny)
    }

}
