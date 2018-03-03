package uk.co.nickthecoder.fizzy.controller.handle

import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression

class Shape1dHandle(val shape1d: Shape1d, position: Dimension2, val isEnd: Boolean)
    : ShapeHandle(shape1d, position) {

    override fun dragTo(event: CMouseEvent, dragPoint: Dimension2) {
        val startEnd = if (isEnd) shape1d.end else shape1d.start

        val formula = Controller.connectFormula(dragPoint, shape1d, event.scale) ?: dragPoint.toFormula()
        shape.document().history.makeChange(
                ChangeExpression(startEnd, formula)
        )
    }
}
