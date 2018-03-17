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
import uk.co.nickthecoder.fizzy.controller.handle.BezierGeometryHandle
import uk.co.nickthecoder.fizzy.controller.handle.GeometryHandle
import uk.co.nickthecoder.fizzy.controller.handle.Handle
import uk.co.nickthecoder.fizzy.controller.handle.Shape1dHandle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.geometry.BezierCurveTo
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.util.toFormula

class EditGeometryTool(controller: Controller)
    : Tool(controller) {

    var editingShape: Shape? = null

    var mousePressedPoint = Dimension2.ZERO_mm

    override fun beginTool() {
        if (editingShape == null) {
            // By clearing the selection, the normal control handles will vanish, and we can add our own.
            editingShape = controller.selection.lastOrNull()
            controller.selection.clear()
        }

        editingShape?.let { createHandles(it) }
    }

    fun createHandles(shape: Shape) {
        if (shape is Shape1d) {
            controller.handles.add(Shape1dHandle(shape, shape.start.value, controller, false))
            controller.handles.add(Shape1dHandle(shape, shape.end.value, controller, true))
        }

        var previousPart: GeometryPart? = null
        shape.geometry.parts.forEach { part ->
            // Do not include the start and end of Shape1d, because that is very confusing!
            if (shape is Shape1d && (part.point.value.x == Dimension.ZERO_mm || part.point.value.x.isNear(shape.length.value))) {
                // Ignore it
            } else {
                controller.handles.add(GeometryHandle(shape, part.point, controller))
            }
            if (part is BezierCurveTo) {
                controller.handles.add(BezierGeometryHandle(shape, part.a, (previousPart ?: part).point, controller))
                controller.handles.add(BezierGeometryHandle(shape, part.b, part.point, controller))
            }

            previousPart = part
        }
    }

    override fun onMousePressed(event: CMouseEvent) {
        mousePressedPoint = event.point

        // If we've pressed an existing handle, or only the current shape's geometry, then do nothing.
        // onDragDetected will take over from here.
        controller.handles.forEach { handle ->
            if (handle.isAt(mousePressedPoint, event.scale)) {
                return
            }
        }
        editingShape?.let {
            if (it.isAt(event.point, controller.minDistance)) {
                return
            }
        }

        // So, we haven't pressed any existing handles, or along the current shape, so lets see if we've clicked a different shape.
        controller.handles.clear()
        editingShape = null
        editingShape = controller.findShapesAt(event.point).lastOrNull()

        editingShape?.let { createHandles(it) }

        controller.dirty.value++

    }

    fun convertToBezierCurve(shape: Shape, part: GeometryPart, prevPoint: Dimension2): BezierCurveTo {
        // TODO Use History.
        val index = shape.geometry.parts.indexOf(part)
        shape.geometry.parts.removeAt(index)
        val a = (prevPoint * 2.0 + part.point.value) / 3.0
        val b = (prevPoint + part.point.value * 2.0) / 3.0
        val bezier = BezierCurveTo(a, b, part.point.value)
        shape.geometry.parts.add(index, bezier)

        return bezier
    }

    override fun onDragDetected(event: CMouseEvent) {

        controller.handles.forEach { handle ->
            if (handle.isAt(mousePressedPoint, event.scale)) {
                if (handle is GeometryHandle) {
                    controller.tool = EditGeometryDragHandleTool(this, handle, mousePressedPoint)
                } else {
                    controller.tool = DragHandleTool(controller, handle, mousePressedPoint, nextTool = this)
                }
                return
            }
        }

        // If we've started dragging a LineTo, then convert it into a Bezier curve, and drag the control handle.
        editingShape?.let { shape ->
            var prev: Dimension2? = null
            shape.geometry.parts.forEach { part ->
                val local = shape.fromPageToLocal.value * mousePressedPoint
                if (prev != null) {
                    if (part is LineTo && part.isAlong(shape, local, prev!!, shape.lineWidth.value, controller.minDistance)) {
                        val length = (part.point.value - prev!!).length()
                        val along = (local - prev!!).length().ratio(length)

                        val bezier = convertToBezierCurve(shape, part, prev!!)
                        createHandles(shape)

                        val point = if (along < 0.5) bezier.a else bezier.b
                        controller.handles.forEach { handle ->
                            if (handle is GeometryHandle && handle.point == point) {
                                controller.tool = EditGeometryDragHandleTool(this, handle, handle.position)
                                controller.tool.onMouseDragged(event)
                            }
                        }
                        return
                    }
                }
                prev = part.point.value
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

            editGeometryTool.editingShape?.let { shape ->

                val changes = mutableListOf<Pair<PropExpression<*>, String>>()

                if (shape is Shape1d) {

                    fun adjustPoint(point: Dimension2Expression): Pair<Dimension2Expression, String> {
                        val xRatio = point.value.x.ratio(shape.length.value)
                        val yRatio = point.value.y.ratio(shape.length.value)
                        return point to "Length * Vector2( ${xRatio.toFormula()}, ${yRatio.toFormula()} )"
                    }

                    // Express the geometry parts' x value in terms of length
                    shape.geometry.parts.forEach { part ->
                        changes.add(adjustPoint(part.point))
                        if (part is BezierCurveTo) {
                            changes.add(adjustPoint(part.a))
                            changes.add(adjustPoint(part.b))
                        }
                    }

                } else {
                    // By now, we've changed many of the geometries, and their points are now constants. These should be
                    // expressed in terms of the shape's size (so that when the shape grows, the geometry also grows.
                    // But size will also be wrong.

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
                    val newSize = Dimension2((maxX - minX).max(Dimension.ONE_POINT), (maxY - minY).max(Dimension.ONE_POINT))
                    val locPin = shape.transform.locPin.value - newOrigin
                    val locRatio = locPin.ratio(newSize)

                    changes.add(shape.transform.locPin to "Size * ${locRatio.toFormula()}")
                    changes.add(shape.size to newSize.toFormula())

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
