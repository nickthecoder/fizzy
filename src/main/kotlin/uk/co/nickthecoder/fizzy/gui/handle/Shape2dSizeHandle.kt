package uk.co.nickthecoder.fizzy.gui.handle

import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape2d
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions

/**
 * Resize a Shape2d.
 * Note, this can affect the Pin as well as the Size
 *
 * dx and dy are either -1, 0 or 1.
 * 0 means is should not affect that axis.
 * -1 means adjust the left/top edge.
 * 1 means the right/bottom edge.
 */
class Shape2dSizeHandle(val shape2d: Shape2d, position: Dimension2, val dx: Int, val dy: Int)
    : ShapeHandle(shape2d, position) {

    var aspectRatio = 1.0

    override fun beginDrag(pagePosition: Dimension2) {
        aspectRatio = shape2d.size.value.aspectRatio()
    }

    override fun dragTo(pagePosition: Dimension2, constrain: Boolean) {
        val local = shape2d.fromPageToLocal.value * pagePosition

        var deltaX = if (dx == 1) {
            local.x - shape2d.size.value.x
        } else if (dx == -1) {
            -local.x
        } else {
            Dimension.ZERO_mm
        }

        var deltaY = if (dy == 1) {
            local.y - shape2d.size.value.y
        } else if (dy == -1) {
            -local.y
        } else {
            Dimension.ZERO_mm
        }

        if (constrain) {
            if ((shape2d.size.value.x + deltaX) / aspectRatio > shape2d.size.value.y + deltaY) {
                deltaX = (shape2d.size.value.y + deltaY) * aspectRatio - shape2d.size.value.x
            } else {
                deltaY = (shape2d.size.value.x + deltaX) / aspectRatio - shape2d.size.value.y
            }
        }


        val moveX = if (dx == 1) {
            deltaX * (shape2d.transform.locPin.value.x / shape2d.size.value.x)
        } else if (dx == -1) {
            -deltaX * (shape2d.transform.locPin.value.x / shape2d.size.value.x)
        } else {
            Dimension.ZERO_mm
        }

        val moveY = if (dy == 1) {
            deltaY * (shape2d.transform.locPin.value.y / shape2d.size.value.y)
        } else if (dy == -1) {
            -deltaY * (shape2d.transform.locPin.value.y / shape2d.size.value.y)
        } else {
            Dimension.ZERO_mm
        }

        val newPin =
                shape2d.fromLocalToParent.value *
                        (shape2d.fromParentToLocal.value * shape2d.transform.pin.value + Dimension2(moveX, moveY))
        val newSize = Dimension2(
                shape2d.size.value.x + deltaX,
                shape2d.size.value.y + deltaY)

        shape2d.document().history.makeChange(ChangeExpressions(listOf(
                shape2d.size to newSize.toExpression(),
                shape2d.transform.pin to newPin.toExpression()
        )))

    }

}