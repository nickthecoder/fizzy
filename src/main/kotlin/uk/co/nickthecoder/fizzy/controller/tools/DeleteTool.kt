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
import uk.co.nickthecoder.fizzy.model.NO_SHAPE
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.history.DeleteShape

class DeleteTool(controller: Controller)
    : Tool(controller) {

    override val cursor = ToolCursor.DELETE

    private var pressedShape: Shape? = null

    override fun onMouseMoved(event: CMouseEvent) {
        val shape = controller.findShapeAt(event.point)
        if (shape == null) {
            controller.highlightShape.value = NO_SHAPE
        } else {
            controller.highlightShape.value = shape
        }
    }

    override fun onMousePressed(event: CMouseEvent) {
        controller.findShapeAt(event.point)?.let {
            pressedShape = it
        }
    }

    override fun onMouseDragged(event: CMouseEvent) {
        val draggedShaped = controller.findShapeAt(event.point)
        if (draggedShaped === pressedShape && draggedShaped != null) {
            controller.highlightShape.value = draggedShaped
        } else {
            controller.highlightShape.value = NO_SHAPE
        }
    }

    override fun onMouseReleased(event: CMouseEvent) {
        val releasedShape = controller.findShapeAt(event.point)
        if (releasedShape != null && releasedShape == pressedShape) {
            controller.page.document.history.makeChange(DeleteShape(releasedShape))
        }
        controller.selection.remove(releasedShape)
        controller.highlightShape.value = NO_SHAPE
    }

}

