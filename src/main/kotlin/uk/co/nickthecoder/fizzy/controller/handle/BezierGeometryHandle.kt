package uk.co.nickthecoder.fizzy.controller.handle

import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.view.DrawContext

class BezierGeometryHandle(
        shape: Shape,
        point: Dimension2Expression,
        val otherPoint: Dimension2Expression,
        controller: Controller)

    : GeometryHandle(shape, point, controller) {

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.beginPath()
            dc.moveTo(Dimension2.ZERO_mm)
            val m = shape.fromLocalToPage.value
            dc.lineTo(m * otherPoint.value - m * point.value)
            dc.endPath(true, false)
            super.draw(dc)
        }
    }
}
