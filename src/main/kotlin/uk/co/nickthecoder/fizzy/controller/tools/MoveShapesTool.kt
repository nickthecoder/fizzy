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
package uk.co.nickthecoder.fizzy.controller.tools

import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.ShapeParent
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions


class MoveShapesTool(controller: Controller, var previousPoint: Dimension2)
    : Tool(controller) {

    override val cursor: ToolCursor = ToolCursor.MOVE

    val document = controller.page.document

    init {
        document.history.beginBatch()
    }

    override fun onMouseDragged(event: CMouseEvent) {
        val now = event.point
        val delta = calculateSnap(controller.selection.lastOrNull(), controller.parent, now - previousPoint)

        // We must move the lines first, because if they are joined, and the thing they join to is earlier,
        // then they will be moved twice.
        controller.selection.filterIsInstance<Shape1d>().forEach { shape ->

            document.history.makeChange(

                    ChangeExpressions(listOf(
                            shape.start to (shape.start.value + delta).toFormula(),
                            shape.end to (shape.end.value + delta).toFormula()
                    ))
            )
        }
        controller.selection.forEach { shape ->
            if (shape !is Shape1d) {
                document.history.makeChange(
                        ChangeExpressions(
                                listOf(shape.transform.pin to (shape.transform.pin.value + delta).toFormula())
                        )
                )
            }
        }

        previousPoint += delta
    }

    override fun onMouseReleased(event: CMouseEvent) {

        // Connect any lines to nearby connection points
        controller.selection.filterIsInstance<Shape1d>().forEach { shape ->

            Controller.connectFormula(shape.parent.fromLocalToPage.value * shape.start.value, shape, event.scale)?.let {
                document.history.makeChange(
                        ChangeExpressions(listOf(
                                shape.start to it
                        )))
            }
            Controller.connectFormula(shape.parent.fromLocalToPage.value * shape.end.value, shape, event.scale)?.let {
                document.history.makeChange(
                        ChangeExpressions(listOf(
                                shape.end to it
                        )))
            }
        }

        document.history.endBatch()
        controller.tool = SelectTool(controller)
    }

}

/**
 * Calculates how much [delta] should be adjusted by to snap the shape.
 *
 *
 *
 * @param delta The change in position of shape in page coordinates
 * @return The adjustment to make to the shape, so that it snaps to something.
 * Returns [delta], if there are no suitable snapping points nearby.
 */
fun calculateSnap(shape: Shape?, parent: ShapeParent, delta: Dimension2): Dimension2 {
    shape ?: return delta

    var bestDeltaX = delta.x
    var bestScoreX = Double.MAX_VALUE

    var bestDeltaY = delta.y
    var bestScoreY = Double.MAX_VALUE

    fun maybeAdjustEither(diff: Dimension2) {
        val scoreX = Math.abs(diff.x.inDefaultUnits)
        if (scoreX < 10.0 && scoreX < bestScoreX) {
            bestScoreX = scoreX
            bestDeltaX = delta.x + diff.x
        }
        val scoreY = Math.abs(diff.y.inDefaultUnits)
        if (scoreY < 10.0 && scoreY < bestScoreY) {
            bestScoreY = scoreY
            bestDeltaY = delta.y + diff.y
        }
    }

    // TODO Add extra snap types, such as a Grid, Rulers etc.

    shape.snapPoints.forEach { sp ->
        val pageSP = shape.fromLocalToPage.value * sp.point.value + delta

        parent.children.forEach { child ->
            if (child != shape) {
                child.snapPoints.forEach { childSP ->
                    val pageChildSP = child.fromLocalToPage.value * childSP.point.value
                    maybeAdjustEither(pageChildSP - pageSP)
                }
            }
        }
    }

    return Dimension2(bestDeltaX, bestDeltaY)
}
