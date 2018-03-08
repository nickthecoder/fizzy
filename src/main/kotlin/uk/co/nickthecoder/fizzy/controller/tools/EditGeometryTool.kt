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
import uk.co.nickthecoder.fizzy.controller.handle.GeometryHandle
import uk.co.nickthecoder.fizzy.controller.handle.Handle
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape

class EditGeometryTool(controller: Controller)
    : Tool(controller) {

    val shapes = mutableListOf<Shape>()

    var mousePressedPoint = Dimension2.ZERO_mm

    override fun beginTool() {
        // By clearing the selection, the normal control handles will vanish, and we can add our own.
        shapes.clear()
        shapes.addAll(controller.page.document.selection)
        controller.page.document.selection.clear()

        shapes.forEach { shape ->
            shape.geometries.forEach { geo ->
                geo.parts.forEach { part ->
                    controller.handles.add(GeometryHandle(shape, part, controller))
                }
            }
        }
    }

    override fun onMousePressed(event: CMouseEvent) {
        mousePressedPoint = event.point
    }

    override fun onDragDetected(event: CMouseEvent) {

        controller.handles.forEach { handle ->
            if (handle.isAt(mousePressedPoint, event.scale)) {
                controller.tool = EditGeometryDragHandleTool(this, handle, mousePressedPoint)
                return
            }
        }
    }

    override fun endTool(replacement: Tool) {
        if (replacement !is EditGeometryDragHandleTool) {
            controller.handles.clear()
            controller.page.document.selection.addAll(shapes)
            shapes.clear()
        }
    }

    class EditGeometryDragHandleTool(val editGeometryTool: EditGeometryTool, val handle: Handle, val startPosition: Dimension2)
        : Tool(editGeometryTool.controller) {

        val offset = startPosition - handle.position

        init {
            controller.page.document.history.beginBatch()
            handle.beginDrag(startPosition)
        }

        override fun onMouseDragged(event: CMouseEvent) {
            handle.dragTo(event, event.point - offset)
        }

        override fun onMouseReleased(event: CMouseEvent) {
            controller.tool = editGeometryTool
            controller.page.document.history.endBatch()
        }

        override fun endTool(replacement: Tool) {
            if (replacement !== editGeometryTool) {
                editGeometryTool.endTool(replacement)
            }
        }
    }
}
