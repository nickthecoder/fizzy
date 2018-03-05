package uk.co.nickthecoder.fizzy.controller.handle

import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.model.ControlPoint
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.RealShape
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression

class ControlPointHandle(shape: RealShape, val controlPoint: ControlPoint)
    : ShapeHandle(shape, shape.fromLocalToPage.value * controlPoint.point.value) {

    override fun dragTo(event: CMouseEvent, dragPoint: Dimension2) {
        val local = controlPoint.constrain(shape.fromPageToLocal.value * dragPoint)

        shape.document().history.makeChange(
                ChangeExpression(controlPoint.point, local.toFormula())
        )
    }
}
