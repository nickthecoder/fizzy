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
import uk.co.nickthecoder.fizzy.model.Color
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.history.CreateShape

/**
 * Adds a Shape1d to the document by dragging.
 */
class GrowShape1dTool(
        controller: Controller,
        val masterShape: Shape1d,
        val strokeColor: Color?,
        val fillColor: Color?)
    : Tool(controller) {

    override val cursor = ToolCursor.GROW

    var start: Dimension2? = null
    var newShape: Shape1d? = null

    init {
        controller.page.document.selection.clear()
    }

    override fun beginTool() {
        controller.showConnectionPoints.value = true
    }

    override fun endTool(replacement: Tool) {
        controller.showConnectionPoints.value = false
        controller.highlightGeometry.value = Controller.NO_GEOMETRY
    }

    override fun onMousePressed(event: CMouseEvent) {
        start = event.point
        controller.page.document.history.beginBatch()
    }

    override fun onDragDetected(event: CMouseEvent) {
        start?.let { start ->
            val newShape = controller.page.copyMasterShape(masterShape) as Shape1d

            // Either a connection to another shape, or the point of the onMousePressed event.
            val connectFormula = Controller.connectFormula(start, newShape, event.scale)

            newShape.start.formula = connectFormula ?: start.toFormula()
            newShape.end.formula = event.point.toFormula()

            strokeColor?.let { newShape.strokeColor.formula = it.toFormula() }
            fillColor?.let { newShape.fillColor.formula = it.toFormula() }

            this.newShape = newShape

            controller.page.document.history.makeChange(
                    CreateShape(newShape, controller.page)
            )
        }
    }

    override fun onMouseDragged(event: CMouseEvent) {
        newShape?.let {
            // Note, we don't need to change the end point using a Change, because the Batch will only be completed
            // when the drag is completed. At which point the end point is set correctly.

            // Either a connection to another shape, or the point of the mouse event.
            val (connectFormula, _, geometry) = Controller.connectData(event.point, it, event.scale)

            controller.highlightGeometry.value = geometry ?: Controller.NO_GEOMETRY

            it.end.formula = connectFormula ?: event.point.toFormula()
        }
    }

    override fun onMouseReleased(event: CMouseEvent) {
        controller.highlightGeometry.value = Controller.NO_GEOMETRY
        newShape = null
        controller.page.document.history.endBatch()
    }
}
