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
package uk.co.nickthecoder.fizzy.controller.handle

import uk.co.nickthecoder.fizzy.controller.CMouseEvent
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

    override fun beginDrag(startPoint: Dimension2) {
        aspectRatio = shape2d.size.value.aspectRatio()
    }

    override fun dragTo(event: CMouseEvent, dragPoint: Dimension2) {
        val local = shape2d.fromPageToLocal.value * dragPoint

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

        if (event.isConstrain) {
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
                shape2d.size to newSize.toFormula(),
                shape2d.transform.pin to newPin.toFormula()
        )))

    }

}