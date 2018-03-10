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
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.prop.DoubleExpression
import uk.co.nickthecoder.fizzy.prop.StringExpression
import uk.co.nickthecoder.fizzy.util.MyTestCase
import uk.co.nickthecoder.fizzy.util.toFormula

class TestShape : MyTestCase() {

    /**
     * Test fields and methods of Shape and RealShape.
     * (not those specific to Shape1d or Shape2d).
     */
    @Test
    fun testShapeTopLevelProps() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")
        page.children.add(box)

        // Now use scratches to test each property.
        // These in the same order as they appear in ShapePropType

        //assertEquals(1.0, test("id", "ID"))

        //assertEquals(page, test("page", "Page"))

        //assertEquals(doc, test("doc", "Document"))

        //assertEquals(page, test("parent", "Parent"))

        assertEquals(100.0, testDouble(box, "Pin.X.mm"))
        assertEquals(200.0, testDouble(box, "Pin.Y.mm"))

        assertEquals(5.0, testDouble(box, "LocPin.X.mm"))
        assertEquals(10.0, testDouble(box, "LocPin.Y.mm"))

        assertEquals(1.0, testDouble(box, "Scale.X"))
        assertEquals(1.0, testDouble(box, "Scale.Y"))

        assertEquals(0.0, testDouble(box, "Rotation.Degrees"))

        //assertEquals(inner, test("findShape", "this.findShape(\"inner\""))
        //assertEquals(inner, test("findShape2", "findShape(\"inner\""))
        //assertEquals(inner, test("findShape", "Parent.findShape(\"inner\"")) // Recurse from Page downwards.

        box.lineWidth.formula = "3mm"
        assertEquals(3.0, testDouble(box, "LineWidth.mm"))

        box.strokeColor.formula = "BLACK"
        assertEquals(Color.BLACK, testPaint(box, "StrokeColor"))

        box.fillColor.formula = "WHITE"
        assertEquals(Color.WHITE, testPaint(box, "FillColor"))

        box.geometries[0].fill.formula = "true"
        assertEquals(true, testBoolean(box, "Geometry1.Fill"))
        box.geometries[0].fill.formula = "false"
        assertEquals(false, testBoolean(box, "Geometry1.Fill"))

        box.geometries[0].stroke.formula = "true"
        assertEquals(true, testBoolean(box, "Geometry1.Stroke"))
        box.geometries[0].stroke.formula = "false"
        assertEquals(false, testBoolean(box, "Geometry1.Stroke"))

        box.geometries[0].connect.formula = "true"
        assertEquals(true, testBoolean(box, "Geometry1.Connect"))
        box.geometries[0].connect.formula = "false"
        assertEquals(false, testBoolean(box, "Geometry1.Connect"))

        // ControlPoints
        box.controlPoints.add(ControlPoint("Dimension2(2mm,3mm)"))
        box.controlPoints.add(ControlPoint("Dimension2(5mm,6mm)"))
        assertEquals(2.0, testDouble(box, "ControlPoint.Point1.X.mm"))
        assertEquals(3.0, testDouble(box, "ControlPoint.Point1.Y.mm"))
        assertEquals(5.0, testDouble(box, "ControlPoint.Point2.X.mm"))
        assertEquals(6.0, testDouble(box, "ControlPoint.Point2.Y.mm"))

        // ConnectionPoints
        box.connectionPoints.add(ConnectionPoint("Dimension2(7mm,8mm)"))
        box.connectionPoints.add(ConnectionPoint("Dimension2(9mm,10mm)"))
        assertEquals(7.0, testDouble(box, "ConnectionPoint.Point1.X.mm"))
        assertEquals(8.0, testDouble(box, "ConnectionPoint.Point1.Y.mm"))
        assertEquals(9.0, testDouble(box, "ConnectionPoint.Point2.X.mm"))
        assertEquals(10.0, testDouble(box, "ConnectionPoint.Point2.Y.mm"))
    }


    /**
     * Renames a shape, and ensures that the expression becomes dirty, and therefore throws.
     */
    @Test
    fun testRenameShape() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")
        val inner = createBox(box, "Dimension2(1mm,2mm)", "Dimension2(10mm,20mm)")
        page.children.add(box)
        box.children.add(inner)

        inner.name.formula = "inner".toFormula()

        fun test(name: String, exp: String): Any {
            box.scratches.add(Scratch(name, StringExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        box.scratches.add(Scratch("myName", StringExpression("this.findShape(\"inner\").Name")))
        assertEquals("inner", box.findScratch("myName")!!.expression.value)

        inner.name.formula = "renamed".toFormula()
        assertFails { box.findScratch("myName")!!.expression.value }
    }

    /**
     * Test fields and methods specific to Shape1d
     */
    @Test
    fun testShape1dTopLevelProps() {
        val doc = Document()
        val page = Page(doc)

        val line = createLine(page, "Dimension2(2mm,3mm)", "Dimension2(5mm,7mm)", "2mm")


        fun test(name: String, exp: String): Any {
            line.scratches.add(Scratch(name, DoubleExpression(exp)))
            return line.findScratch(name)!!.expression.value
        }

        assertEquals(2.0, test("startX", "Start.X.mm"))
        assertEquals(3.0, test("startY", "Start.Y.mm"))

        assertEquals(5.0, test("endX", "End.X.mm"))
        assertEquals(7.0, test("endY", "End.Y.mm"))

        assertEquals(5.0, test("sizeX", "Size.X.mm")) // The length of the line
        assertEquals(2.0, test("sizeY", "Size.Y.mm")) // The width of the line.

        assertEquals(5.0, test("lengthY", "Length.mm"))

    }

    /**
     * Test fields and methods specific to Shape2d
     */
    @Test
    fun testShape2dTopLevelProps() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")

        fun test(name: String, exp: String): Any {
            box.scratches.add(Scratch(name, DoubleExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        assertEquals(10.0, test("sizeX", "Size.X.mm"))
        assertEquals(20.0, test("sizeY", "Size.Y.mm"))

    }

    @Test
    fun testDeleteGeometryPart() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")
        page.children.add(box)

        box.scratches.add(Scratch("G2", DimensionExpression("Geometry1.Point1.X")))
        assertEquals(Dimension(0.0, Dimension.Units.mm), box.scratches[0].expression.value)
        box.geometries[0].parts.removeAt(0)
        assertEquals(Dimension(10.0, Dimension.Units.mm), box.scratches[0].expression.value)
    }

    @Test
    fun testDeleteGeometryPart2() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")
        page.children.add(box)

        box.scratches.add(Scratch("G2", DoubleExpression("Geometry1.Point1.X.mm")))
        assertEquals(0.0, box.scratches[0].expression.value)
        box.geometries[0].parts.removeAt(0)
        assertEquals(10.0, box.scratches[0].expression.value)
    }

    @Test
    fun testDeleteGeometry() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")
        page.children.add(box)
        val geometry = Geometry()
        geometry.parts.add(MoveTo("Dimension2(-20mm, -30mm)"))
        geometry.parts.add(LineTo("Dimension2(-40mm, -30mm)"))
        box.geometries.add(geometry)

        box.scratches.add(Scratch("G2", DimensionExpression("Geometry2.Point1.X")))
        assertEquals(Dimension(-20.0, Dimension.Units.mm), box.scratches[0].expression.value)
        box.geometries.removeAt(0)
        assertFails { box.scratches[0].expression.value }

    }

    /**
     * This is the same as testDeleteGeometry, but asks for the X in mm, rather than just X.
     * This used to fail
     */
    @Test
    fun testDeleteGeometry2() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page, "Dimension2(10mm,20mm)", "Dimension2(100mm,200mm)")
        page.children.add(box)
        val geometry = Geometry()
        geometry.parts.add(MoveTo("Dimension2(-20mm, -30mm)"))
        geometry.parts.add(LineTo("Dimension2(-40mm, -30mm)"))
        box.geometries.add(geometry)

        box.scratches.add(Scratch("G2", DoubleExpression("Geometry2.Point1.X.mm")))
        println(box.scratches[0].expression.value)
        assertEquals(-20.0, box.scratches[0].expression.value)

        box.geometries.removeAt(0)
        assertFails { box.scratches[0].expression.value }

    }
}
