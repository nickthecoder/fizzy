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
package uk.co.nickthecoder.fizzy.controller

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.controller.handle.*
import uk.co.nickthecoder.fizzy.controller.tools.SelectTool
import uk.co.nickthecoder.fizzy.controller.tools.Tool
import uk.co.nickthecoder.fizzy.controller.tools.ToolCursor
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.prop.PropVariable
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType

/**
 * This is a non-gui class, which takes mouse events from GlassCanvas, and performs actions via Tools.
 * It has no dependencies on JavaFX, and can therefore be used easily from JUnit (i.e. bypassing GlassCanvas).
 */
class Controller(val page: Page) {

    var scale = 1.0

    var tool: Tool = SelectTool(this)
        set(v) {
            field.endTool(v)
            v.beginTool()
            field = v
            cursorProp.value = v.cursor
        }

    val cursorProp = PropVariable<ToolCursor>(tool.cursor)

    val handles = mutableListOf<Handle>()

    /**
     * The minimum distance away from a line for it to be considered close enough to select it.
     * This should be scaled with
     */
    val minDistance: Dimension
        get() = LINE_NEAR / scale

    /**
     * Listen to the currently selected shapes. When they change, we need to draw their handles and bounding box etc.
     */
    val shapeListener = object : ChangeListener<Shape> {
        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            if (page.document.selection.contains(item)) {
                createShapeHandles(item)
                dirty.value++
            }
        }
    }

    /**
     * When the selection changes, we need to draw, to update the bounding boxes and handles on display.
     */
    val selectionListener = object : CollectionListener<Shape> {

        override fun added(collection: FCollection<Shape>, item: Shape) {
            item.changeListeners.add(shapeListener)
            createShapeHandles(item)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            item.changeListeners.remove(shapeListener)
            removeShapeHandles(item)
        }
    }

    /**
     * Whenever the handles, or the tool need redrawing, increment the counter, so that listeners
     * can redraw themselves.
     * GlassCanvas listens to this.
     * Several [Tool]s increment this
     */
    val dirty = PropVariable(0)

    /**
     * When set, the connection points of all shapes should be shown as tiny crosses.
     */
    val showConnectionPoints = PropVariable(false)

    /**
     * When dragging a line end point, if it hovers over a connectable geometry, then
     * set this to it, and GlassCanvas will highlight it. Reset it back to [NO_GEOMETRY]
     * afterwards.
     */
    val highlightGeometry = PropVariable(NO_GEOMETRY)

    init {
        tool.beginTool()
        page.document.selection.listeners.add(selectionListener)
        page.document.selection.forEach {
            createShapeHandles(it)
        }
    }

    fun onMousePressed(event: CMouseEvent) {
        tool.onMousePressed(event)
    }

    fun onMouseReleased(event: CMouseEvent) {
        tool.onMouseReleased(event)
    }

    fun onMouseMoved(event: CMouseEvent) {
        tool.onMouseMoved(event)
    }

    fun onMouseClicked(event: CMouseEvent) {
        tool.onMouseClicked(event)
    }

    fun onDragDetected(event: CMouseEvent) {
        tool.onDragDetected(event)
    }

    fun onMouseDragged(event: CMouseEvent) {
        tool.onMouseDragged(event)
    }


    fun createShapeHandles(shape: Shape) {
        removeShapeHandles(shape)

        if (shape is Shape2d) {
            val corners = shapeCorners(shape)
            handles.add(Shape2dSizeHandle(shape, corners[0], -1, -1))
            handles.add(Shape2dSizeHandle(shape, corners[1], 1, -1))
            handles.add(Shape2dSizeHandle(shape, corners[2], 1, 1))
            handles.add(Shape2dSizeHandle(shape, corners[3], -1, 1))

            handles.add(Shape2dSizeHandle(shape, (corners[0] + corners[1]) / 2.0, 0, -1))
            handles.add(Shape2dSizeHandle(shape, (corners[1] + corners[2]) / 2.0, 1, 0))
            handles.add(Shape2dSizeHandle(shape, (corners[2] + corners[3]) / 2.0, 0, 1))
            handles.add(Shape2dSizeHandle(shape, (corners[3] + corners[0]) / 2.0, -1, 0))

            handles.add(RotationHandle(shape,
                    (corners[0] + corners[1]) / 2.0 +
                            (corners[1] - corners[2]).normalise()
                                    * Dimension(GlassCanvas.ROTATE_DISTANCE)))

        } else if (shape is Shape1d) {
            val ends = shape1dEnds(shape)
            handles.add(Shape1dHandle(shape, ends[0], this, false))
            handles.add(Shape1dHandle(shape, ends[1], this, true))
        }

        shape.controlPoints.forEach { cp ->
            handles.add(ControlPointHandle(shape, cp))
        }

    }

    fun removeShapeHandles(shape: Shape) {
        handles.removeIf { it.isFor(shape) }
    }

    fun shapeCorners(shape: Shape): Array<Dimension2> {
        return arrayOf<Dimension2>(
                shape.fromLocalToPage.value * Dimension2.ZERO_mm,
                shape.fromLocalToPage.value * (shape.size.value * Vector2(1.0, 0.0)),
                shape.fromLocalToPage.value * (shape.size.value * Vector2(1.0, 1.0)),
                shape.fromLocalToPage.value * (shape.size.value * Vector2(0.0, 1.0))
        )
    }

    fun shape1dEnds(shape: Shape1d): Array<Dimension2> {
        return arrayOf<Dimension2>(
                shape.parent.fromLocalToPage.value * shape.start.value,
                shape.parent.fromLocalToPage.value * shape.end.value
        )
    }

    fun isWithin(shape: Shape, a: Dimension2, b: Dimension2): Boolean {
        shapeCorners(shape).forEach {
            if (((it.x < a.x) xor (it.x > b.x)) || ((it.y < a.y) xor (it.y > b.y))) {
                return false
            }
        }
        return true
    }

    companion object {

        val NO_GEOMETRY = Geometry.NO_GEOMETRY

        val HANDLE_SIZE = 3.0 // Half width (or height) of handles excluding the stroke.
        val HANDLE_NEAR = HANDLE_SIZE + 2.0 // The size when testing if the mouse is at the handle
        val LINE_NEAR = Dimension(4.0) // The distance away from a line, and still be able to select it.


        fun connectFormula(pagePoint: Dimension2, shape: Shape, scale: Double): String? {
            return connectData(pagePoint, shape, scale).first
        }

        fun connectData(pagePoint: Dimension2, shape: Shape, scale: Double): Triple<String?, ConnectionPoint?, Geometry?> {

            // Look for connection points
            shape.page().findNearestConnectionPoint(pagePoint, shape)?.let { (connectionPoint, distance) ->
                if (distance < HANDLE_NEAR / scale) {
                    return Triple(connectionPoint.connectToFormula(), connectionPoint, null)
                }
            }

            // Look for geometries that can be connected to.
            shape.page().findNearestConnectionGeometry(pagePoint, shape)?.let { (geometry, distance, along) ->
                if (distance < HANDLE_NEAR / scale) {
                    return Triple(geometry.connectAlongFormula(along), null, geometry)
                }
            }

            return Triple(null, null, null)

        }
    }
}
