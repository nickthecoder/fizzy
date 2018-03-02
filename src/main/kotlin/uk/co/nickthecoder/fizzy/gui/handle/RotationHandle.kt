package uk.co.nickthecoder.fizzy.gui.handle

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression
import uk.co.nickthecoder.fizzy.view.DrawContext

class RotationHandle(shape: Shape, position: Dimension2)
    : ShapeHandle(shape, position) {

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.rotate(Angle.degrees(45.0))
            super.draw(dc)
        }
    }

    override fun dragTo(pagePosition: Dimension2, constrain : Boolean) {
        val local = shape.fromPageToLocal.value * pagePosition
        val angle = (local - shape.transform.locPin.value).angle() + Angle.degrees(90.0)
        shape.document().history.makeChange(ChangeExpression(
                shape.transform.rotation,
                (shape.transform.rotation.value + angle).toFormula()))
    }
}
