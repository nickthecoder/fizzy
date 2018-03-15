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
import uk.co.nickthecoder.fizzy.model.geometry.BezierCurveTo
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.PropExpression

class EditGeometryTool(controller: Controller)
    : Tool(controller) {

    var editingShape: Shape? = null

    var mousePressedPoint = Dimension2.ZERO_mm

    init {
        // By clearing the selection, the normal control handles will vanish, and we can add our own.
        editingShape = controller.selection.lastOrNull()
        controller.selection.clear()

        editingShape?.let { createHandles(it) }
    }

    fun createHandles(shape: Shape) {
        shape.geometry.parts.forEach { part ->
            controller.handles.add(GeometryHandle(shape, part.point, controller))
            if (part is BezierCurveTo) {
                controller.handles.add(GeometryHandle(shape, part.a, controller))
                controller.handles.add(GeometryHandle(shape, part.b, controller))
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
        editingShape = null
        editingShape = controller.findShapesAt(event.point).lastOrNull()

        editingShape?.let { createHandles(it) }

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
            editingShape?.let { controller.selection.add(it) }
            editingShape = null
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
            // By now, we've changed many of the geometries, and their points are now constants. These should be
            // expressed in terms of the shape's size (so that when the shape grows, the geometry also grows.
            // But size will also be wrong.

            editGeometryTool.editingShape?.let { shape ->

                var minX = Dimension(Double.MAX_VALUE)
                var minY = Dimension(Double.MAX_VALUE)
                var maxX = Dimension(-Double.MAX_VALUE)
                var maxY = Dimension(-Double.MAX_VALUE)

                shape.geometry.parts.forEach { part ->
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

                val newOrigin = Dimension2(minX, minY)
                val newSize = Dimension2((maxX - minX).min(Dimension.ONE_POINT), (maxY - minY).min(Dimension.ONE_POINT))
                val locPin = shape.transform.locPin.value - newOrigin
                val locRatio = locPin.ratio(newSize)

                val changes = mutableListOf<Pair<PropExpression<*>, String>>(
                        shape.transform.locPin to "Size * ${locRatio.toFormula()}",
                        shape.size to newSize.toFormula()
                )

                fun adjustPoint(point: Dimension2Expression): Pair<Dimension2Expression, String> {
                    val ratio = (point.value - newOrigin).ratio(newSize)
                    return point to "Size * ${ratio.toFormula()}"
                }

                shape.geometry.parts.forEach { part ->
                    changes.add(adjustPoint(part.point))
                    if (part is BezierCurveTo) {
                        changes.add(adjustPoint(part.a))
                        changes.add(adjustPoint(part.b))
                    }
                }

                shape.document().history.makeChange(ChangeExpressions(changes))

                // Strange bodge to ensure connected line are also updated
                shape.size.forceRecalculation()
            }


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
