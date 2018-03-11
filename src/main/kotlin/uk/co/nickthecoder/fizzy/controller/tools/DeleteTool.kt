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
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.history.DeleteShape

class DeleteTool(controller: Controller)
    : Tool(controller) {

    override val cursor = ToolCursor.DELETE

    private var pressedShape: Shape? = null

    override fun onMouseMoved(event: CMouseEvent) {
        val shape = controller.page.findShapeAt(event.point, controller.minDistance)
        if (shape == null) {
            controller.highlightGeometry.value = Controller.NO_GEOMETRY
        } else {
            controller.highlightGeometry.value = shape.geometry
        }
    }

    override fun onMousePressed(event: CMouseEvent) {
        controller.page.findShapeAt(event.point, controller.minDistance)?.let {
            pressedShape = it
        }
    }

    override fun onMouseDragged(event: CMouseEvent) {
        val draggedShaped = controller.page.findShapeAt(event.point, controller.minDistance)
        if (draggedShaped === pressedShape && draggedShaped != null) {
            controller.highlightGeometry.value = draggedShaped.geometry
        } else {
            controller.highlightGeometry.value = Controller.NO_GEOMETRY
        }
    }

    override fun onMouseReleased(event: CMouseEvent) {
        val releasedShape = controller.page.findShapeAt(event.point, controller.minDistance)
        if (releasedShape != null && releasedShape == pressedShape) {
            controller.page.document.history.makeChange(DeleteShape(releasedShape))
        }
        controller.highlightGeometry.value = Controller.NO_GEOMETRY
    }

}

