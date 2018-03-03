package uk.co.nickthecoder.fizzy.controller.handle

import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression

class Shape1dHandle(val shape1d: Shape1d, position: Dimension2, val isEnd: Boolean)
    : ShapeHandle(shape1d, position) {

    override fun dragTo(pagePosition: Dimension2, constrain: Boolean) {
        val startEnd = if (isEnd) shape1d.end else shape1d.start

        shape.document().history.makeChange(
                ChangeExpression(startEnd, pagePosition.toFormula())
        )
    }
}
