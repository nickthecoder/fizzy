package uk.co.nickthecoder.fizzy.util

import uk.co.nickthecoder.fizzy.model.*

interface MyShapeTest {

    fun createBox(page: Page, size: String, at : String): Shape2d {
        val box = Shape2d(page)
        // Make a box 60mm x 40mm. centered at (40mm,120mm)
        box.size.expression = size
        box.transform.pin.expression = at
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


}
