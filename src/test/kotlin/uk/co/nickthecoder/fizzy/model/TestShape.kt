package uk.co.nickthecoder.fizzy.model

import org.junit.Test
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestShape : MyTestCase() {

    val document = Document()

    val layer1 = Layer(document)

    val box = Shape2d(layer1)
    val line = Shape1d(layer1)

    @Test
    fun testGeometry() {
        // Make a box 60mm x 40mm. centered at (40mm,120mm)
        box.size.expression = "Dimension2(60mm,120mm)"
        box.transform.position.expression = "Dimension2(40mmx60mm)"
        val geometry = Geometry()
        box.geometries.add(geometry)
        // Make a box
        geometry.parts.add(MoveTo("size * -0.5"))
        geometry.parts.add(LineTo("size * Vector2(0.5,-0.5)"))
        geometry.parts.add(LineTo("size * 0.5"))
        geometry.parts.add(LineTo("size * Vector2(-0.5, 0.5)"))
        geometry.parts.add(LineTo("geometry1.point1"))

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
        topLeft.point.expression = "size * Vector2(0, -0.5)"
        assertEquals(0.0, (geometry.parts[0] as MoveTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[0] as MoveTo).point.value.y.mm) // Unchanged
        assertEquals(0.0, (geometry.parts[4] as LineTo).point.value.x.mm)
        assertEquals(-60.0, (geometry.parts[4] as LineTo).point.value.y.mm) // Unchanged

    }
}
