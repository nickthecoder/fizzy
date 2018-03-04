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
import uk.co.nickthecoder.fizzy.prop.BooleanExpression
import uk.co.nickthecoder.fizzy.prop.DoubleExpression
import uk.co.nickthecoder.fizzy.prop.PaintExpression
import uk.co.nickthecoder.fizzy.prop.StringExpression
import uk.co.nickthecoder.fizzy.util.MyShapeTest
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestShape : MyTestCase(), MyShapeTest {

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

        fun testDouble(name: String, exp: String): Any {
            box.addScratch(Scratch(name, DoubleExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        fun testPaint(name: String, exp: String): Any {
            box.addScratch(Scratch(name, PaintExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        fun testBoolean(name: String, exp: String): Any {
            box.addScratch(Scratch(name, BooleanExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        // Now use scratches to test each property.
        // These in the same order as they appear in ShapePropType

        //assertEquals(1.0, test("id", "ID"))

        //assertEquals(page, test("page", "Page"))

        //assertEquals(doc, test("doc", "Document"))

        //assertEquals(page, test("parent", "Parent"))

        assertEquals(100.0, testDouble("pinX", "Pin.X.mm"))
        assertEquals(200.0, testDouble("pinY", "Pin.Y.mm"))

        assertEquals(5.0, testDouble("locPinX", "LocPin.X.mm"))
        assertEquals(10.0, testDouble("locPinY", "LocPin.Y.mm"))

        assertEquals(1.0, testDouble("scaleX", "Scale.X"))
        assertEquals(1.0, testDouble("scaleY", "Scale.Y"))

        assertEquals(0.0, testDouble("rotation", "Rotation.Degrees"))

        //assertEquals(inner, test("findShape", "this.findShape(\"inner\""))
        //assertEquals(inner, test("findShape2", "findShape(\"inner\""))
        //assertEquals(inner, test("findShape", "Parent.findShape(\"inner\"")) // Recurse from Page downwards.

        box.lineWidth.formula = "3mm"
        assertEquals(3.0, testDouble("lineWidth", "LineWidth.mm"))

        box.lineColor.formula = "Color.yellow"
        assertEquals(Color.NamedColors["yellow"], testPaint("lineColor", "LineColor"))

        box.fillColor.formula = "Color.green"
        assertEquals(Color.NamedColors["green"], testPaint("fillColor", "FillColor"))

        box.geometries[0].value.fill.formula = "true"
        assertEquals(true, testBoolean("fill1", "Geometry1.Fill"))
        box.geometries[0].value.fill.formula = "false"
        assertEquals(false, testBoolean("fill2", "Geometry1.Fill"))

        box.geometries[0].value.stroke.formula = "true"
        assertEquals(true, testBoolean("stroke1", "Geometry1.Stroke"))
        box.geometries[0].value.stroke.formula = "false"
        assertEquals(false, testBoolean("stroke2", "Geometry1.Stroke"))

        box.geometries[0].value.connect.formula = "true"
        assertEquals(true, testBoolean("connect1", "Geometry1.Connect"))
        box.geometries[0].value.connect.formula = "false"
        assertEquals(false, testBoolean("connect2", "Geometry1.Connect"))
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

        inner.name.value = "inner"

        fun test(name: String, exp: String): Any {
            box.addScratch(Scratch(name, StringExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        box.addScratch(Scratch("myName", StringExpression("this.findShape(\"inner\").Name")))
        assertEquals("inner", box.findScratch("myName")!!.expression.value)

        inner.name.value = "renamed"
        assertFails { println(box.findScratch("myName")!!.expression.value) }
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
            line.addScratch(Scratch(name, DoubleExpression(exp)))
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
            box.addScratch(Scratch(name, DoubleExpression(exp)))
            return box.findScratch(name)!!.expression.value
        }

        assertEquals(10.0, test("sizeX", "Size.X.mm"))
        assertEquals(20.0, test("sizeY", "Size.Y.mm"))

    }
}
