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

class SelectTool(controller: Controller)
    : Tool(controller) {

    var mousePressedPoint = Dimension2.ZERO_mm

    override fun onContextMenu(event: CMouseEvent): List<Pair<String, () -> Unit>> {
        val shapes = controller.findShapesAt(event.point)
        val latest = controller.selection.lastOrNull()
        val shape: Shape?
        if (shapes.size > 1 && shapes.contains(latest) && event.isConstrain) {
            val i = shapes.indexOf(latest)
            shape = if (i == 0) shapes.last() else shapes[i - 1]
        } else {
            shape = shapes.lastOrNull()
        }

        if (shape != null) {
            val list: MutableList<Pair<String, () -> Unit>> = mutableListOf(
                    "Enter Shape" to {
                        controller.parent = shape
                        controller.selection.clear()
                        controller.selection.addAll(shape.children)
                        Unit
                    },
                    "Edit Shape Sheet" to { controller.otherActions.editShapeSheet(shape) }
            )

            if (shape.customProperties.isNotEmpty()) {
                list.add("Custom Properties …" to { controller.otherActions.editCustomProperties(shape) })
            }
            return list
        }
        return super.onContextMenu(event)
    }

    override fun onMousePressed(event: CMouseEvent) {

        mousePressedPoint = event.point

        controller.handles.forEach { handle ->
            if (handle.isAt(mousePressedPoint, event.scale)) {
                // If this is the beginning of dragging a handle, then leave the selection unchanged.
                return
            }
        }

        val shapes = controller.findShapesAt(event.point)
        if (shapes.isEmpty()) {
            if (!event.isAdjust) {
                controller.selection.clear()
                controller.parent = controller.page // Exit from editing a group if we ARE editing a group.
            }
        } else {
            val previouslyTopSelected = controller.selection.lastOrNull()
            val clickedShape: Shape?
            if (shapes.size > 1 && shapes.contains(previouslyTopSelected) && event.isConstrain) {
                val i = shapes.indexOf(previouslyTopSelected)
                clickedShape = if (i == 0) shapes.last() else shapes[i - 1]
            } else {
                clickedShape = shapes.lastOrNull()
            }

            if (clickedShape != null) {
                if (event.isAdjust) {
                    if (controller.selection.contains(clickedShape)) {
                        controller.selection.remove(clickedShape)
                    } else {
                        controller.selection.add(clickedShape)
                    }
                } else {
                    if (clickedShape !in controller.selection) {
                        controller.selection.clear()
                        controller.selection.add(clickedShape)
                    }
                }
            }
        }
    }

    override fun onDragDetected(event: CMouseEvent) {

        controller.handles.forEach { handle ->
            if (handle.isAt(mousePressedPoint, event.scale)) {
                controller.tool = DragHandleTool(controller, handle, mousePressedPoint)
                return
            }
        }

        if (controller.selection.isEmpty()) {
            controller.tool = BoundingBoxTool(controller, event, mousePressedPoint)
            controller.tool.onMouseDragged(event)
        } else {
            controller.tool = MoveShapesTool(controller, mousePressedPoint)
            controller.tool.onMouseDragged(event)
        }
    }

}
