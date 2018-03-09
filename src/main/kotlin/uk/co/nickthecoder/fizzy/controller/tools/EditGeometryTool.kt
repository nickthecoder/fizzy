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
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.PropExpression

class EditGeometryTool(controller: Controller)
    : Tool(controller) {

    var shape: Shape? = null

    var mousePressedPoint = Dimension2.ZERO_mm

    override fun beginTool() {
        // By clearing the selection, the normal control handles will vanish, and we can add our own.
        shape = controller.page.document.selection.lastOrNull()
        controller.page.document.selection.clear()

        shape?.let {
            it.geometries.forEach { geo ->
                geo.parts.forEach { part ->
                    controller.handles.add(GeometryHandle(it, part, controller))
                }
            }
        }
    }

    override fun onMousePressed(event: CMouseEvent) {
        mousePressedPoint = event.point

        controller.handles.forEach { handle ->
            if (handle.isAt(mousePressedPoint, event.scale)) {
                return
            }
        }
        controller.handles.clear()
        // So, we haven't pressed any existing handles, lets see if we've clicked a different shape.
        shape = null
        shape = controller.page.findShapesAt(event.point, controller.minDistance).lastOrNull()


        shape?.let {
            it.geometries.forEach { geo ->
                geo.parts.forEach { part ->
                    controller.handles.add(GeometryHandle(it, part, controller))
                }
            }
        }

        controller.dirty.value++

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
            shape?.let { controller.page.document.selection.add(it) }
            shape = null
        }
    }

    class EditGeometryDragHandleTool(val editGeometryTool: EditGeometryTool, val handle: Handle, val startPosition: Dimension2)
        : Tool(editGeometryTool.controller) {

        val offset = startPosition - handle.position

        init {
            println("Begin batch")
            controller.page.document.history.beginBatch()
            handle.beginDrag(startPosition)
        }

        override fun onMouseDragged(event: CMouseEvent) {
            handle.dragTo(event, event.point - offset)
        }

        override fun onMouseReleased(event: CMouseEvent) {
            // By now, we've changed many of the geometries, and their points are now constants. These should be
            // expressed in terms of the shape's size (so that when the shape grows, the geometry also grows.
            // But size will also be wrong.
            editGeometryTool.shape?.let { shape ->
                var minX = Dimension(Double.MAX_VALUE)
                var minY = Dimension(Double.MAX_VALUE)
                var maxX = Dimension(-Double.MAX_VALUE)
                var maxY = Dimension(-Double.MAX_VALUE)

                shape.geometries.forEach { geo ->
                    geo.parts.forEach { part ->
                        if (part.point.value.x < minX) {
                            minX = part.point.value.x
                        }
                        if (part.point.value.y < minY) {
                            minY = part.point.value.y
                        }
                        if (part.point.value.x > maxX) {
                            maxX = part.point.value.x
                        }
                        if (part.point.value.y > maxY) {
                            maxY = part.point.value.y
                        }
                    }
                }

                val newOrigin = Dimension2(minX, minY)
                val newSize = Dimension2(maxX - minX, maxY - minY)
                val locPin = shape.transform.locPin.value - newOrigin
                val locRatio = locPin.ratio(newSize)


                val changes = mutableListOf<Pair<PropExpression<*>, String>>(
                        shape.transform.locPin to "Size * ${locRatio.toFormula()}",
                        shape.size to newSize.toFormula()
                )

                shape.geometries.forEach { geo ->
                    geo.parts.forEach { part ->
                        val ratio = (part.point.value - newOrigin).ratio(newSize)
                        changes.add(part.point to "Size * ${ratio.toFormula()}")
                    }
                }
                println("Changes : ${changes.map { it.second }}")

                shape.document().history.makeChange(ChangeExpressions(changes))
            }

            controller.tool = editGeometryTool
            controller.page.document.history.endBatch()
            println("End batch")
        }

        override fun endTool(replacement: Tool) {
            if (replacement !== editGeometryTool) {
                editGeometryTool.endTool(replacement)
            }
        }
    }
}