package uk.co.nickthecoder.fizzy.model

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestPolygon : MyTestCase() {

    @Test
    fun testPolygon() {
        val doc = Document()
        val page = Page(doc)

        val poly1 = Shape.createPolygon(page, 5, dimension("100mm"), star = false)
        val poly2 = Shape.createPolygon(page, 5, dimension("100mm"), star = false)
        page.children.add(poly2)
        page.children.add(poly1)

        // Rotating poly2 should match the un-rotated poly1
        poly2.transform.rotation.formula = (Angle.TAU / 5.0).toFormula()
        assertSamePoint(poly2.geometries[0].parts[0], poly1.geometries[0].parts[1])
        poly2.transform.rotation.formula = (Angle.TAU / 2.5).toFormula()
        assertSamePoint(poly2.geometries[0].parts[0], poly1.geometries[0].parts[2])
    }

    @Test
    fun testStar() {
        val doc = Document()
        val page = Page(doc)

        val poly1 = Shape.createPolygon(page, 5, dimension("100mm"), star = true)
        val poly2 = Shape.createPolygon(page, 5, dimension("100mm"), star = true)
        page.children.add(poly2)
        page.children.add(poly1)

        assertEquals(testDouble(poly1, "Geometry1.Point1.X.mm"), testDouble(poly1, "ConnectionPoint.Point1.X.mm"))
        assertEquals(testDouble(poly1, "Geometry1.Point10.X.mm"), testDouble(poly1, "ConnectionPoint.Point10.X.mm"))

        // Rotating poly2 should match the un-rotated poly1
        poly2.transform.rotation.formula = (Angle.TAU / 5.0).toFormula()
        assertSamePoint(poly2.geometries[0].parts[0], poly1.geometries[0].parts[2])
        assertSamePoint(poly2.geometries[0].parts[1], poly1.geometries[0].parts[3])

        poly2.transform.rotation.formula = (Angle.TAU / 2.5).toFormula()
        assertSamePoint(poly2.geometries[0].parts[0], poly1.geometries[0].parts[4])
        assertSamePoint(poly2.geometries[0].parts[1], poly1.geometries[0].parts[5])

        // Move the control point to the 1st geometry, so we should end up with a regular polygon.
        poly1.controlPoints[0].point.formula = poly1.geometries[0].parts[0].point.value.toFormula()
        assertEquals(0.0, testDouble(poly1, "(ControlPoint.Point1.X - Geometry1.Point1.X).mm"), tiny)
        assertEquals(0.0, testDouble(poly1, "(Geometry1.Point1.X - Geometry1.Point2.X).mm"), tiny)
        assertEquals(0.0, testDouble(poly1, "(Geometry1.Point3.X - Geometry1.Point4.X).mm"), tiny)
        assertEquals(0.0, testDouble(poly1, "(Geometry1.Point5.X - Geometry1.Point6.X).mm"), tiny)
        assertEquals(0.0, testDouble(poly1, "(Geometry1.Point7.X - Geometry1.Point8.X).mm"), tiny)

        // Move the control point to make a pentangle pattern.
        poly1.controlPoints[0].point.formula = poly1.geometries[0].parts[4].point.value.toFormula()
        assertEquals(0.0, testDouble(poly1, "(ControlPoint.Point1.X - Geometry1.Point5.X).mm"), tiny)
        assertEquals(0.0, testDouble(poly1, "(Geometry1.Point2.X - Geometry1.Point5.X).mm"), tiny)
        assertEquals(0.0, testDouble(poly1, "(Geometry1.Point4.X - Geometry1.Point7.X).mm"), tiny)

    }

    @Test
    fun testSimple() {
        val doc = Document()
        val page = Page(doc)

        val poly = Shape2d.create(page)

        val geometry = Geometry()
        poly.addGeometry(geometry)
        geometry.parts.add(MoveTo("ControlPoint.Point1"))

        val cp1 = ControlPoint("Dimension2(10mm,20mm)")
        val point = geometry.parts[0].point
        poly.addControlPoint(cp1)

        assertEquals(10.0, point.value.x.mm)
        cp1.point.formula = "Dimension2(15mm,25mm)"
        assertEquals(15.0, point.value.x.mm)
    }
}
