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

import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.collection.ListListener
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.controller.handle.*
import uk.co.nickthecoder.fizzy.controller.tools.SelectTool
import uk.co.nickthecoder.fizzy.controller.tools.Tool
import uk.co.nickthecoder.fizzy.controller.tools.ToolCursor
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.PropVariable
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.toFormula

/**
 * This is a non-gui class, which takes mouse events from GlassCanvas, and performs actions via Tools.
 * It has no dependencies on JavaFX, and can therefore be used easily from JUnit (i.e. bypassing GlassCanvas).
 */
class Controller(val page: Page, val singleShape: Shape? = null, val otherActions: OtherActions) {

    var scale = 96.0 / 25.4

    var tool: Tool = SelectTool(this)
        set(v) {
            field.endTool(v)
            v.beginTool()
            field = v
            cursorProp.value = v.cursor
        }

    /**
     *  The parent of new shapes
     */
    var parent: ShapeParent = singleShape ?: page

    val selection = MutableFList<Shape>()

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
            if (selection.contains(item)) {
                createShapeHandles(item)
                dirty.value++
            }
        }
    }

    /**
     * When the selection changes, we need to draw, to update the bounding boxes and handles on display.
     */
    val selectionListener = object : ListListener<Shape> {

        override fun added(list: FList<Shape>, item: Shape, index: Int) {
            item.changeListeners.add(shapeListener)
            createShapeHandles(item)
        }

        override fun removed(list: FList<Shape>, item: Shape, index: Int) {
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
     * set this to it, and GlassCanvas will highlight it. Reset it back to [NO_SHAPE]
     * afterwards.
     */
    val highlightShape: PropVariable<Shape> = PropVariable(NO_SHAPE)

    init {
        tool.beginTool()
        selection.listeners.add(selectionListener)
        selection.forEach {
            createShapeHandles(it)
        }
    }

    fun onKeyTyped(event: CKeyEvent) {
        tool.onKeyTyped(event)
    }

    fun onKeyPressed(event: CKeyEvent) {
        tool.onKeyPressed(event)
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

    fun findShapeAt(pagePoint: Dimension2) = findShapesAt(pagePoint).lastOrNull()

    fun findShapesAt(pagePoint: Dimension2): List<Shape> {

        if (parent is Shape) {
            return parent.findShapesAt(pagePoint, minDistance)
        }

        // If we are at the top-level (i.e. not entered a shape), then only test the singleShape if we are in
        // singleShape mode, otherwise test for all shapes on the page (parent will be a Page at this point).
        if (singleShape == null) {
            return parent.findShapesAt(pagePoint, minDistance)
        } else {
            if (singleShape.isAt(pagePoint, minDistance)) {
                return listOf(singleShape)
            } else {
                return emptyList()
            }
        }
    }

    fun createShapeHandles(shape: Shape) {
        removeShapeHandles(shape)

        if (shape is Shape2d) {
            val corners = shapeCorners(shape)
            if (!shape.locks.size.value) {
                handles.add(Shape2dSizeHandle(shape, corners[0], -1, -1))
                handles.add(Shape2dSizeHandle(shape, corners[1], 1, -1))
                handles.add(Shape2dSizeHandle(shape, corners[2], 1, 1))
                handles.add(Shape2dSizeHandle(shape, corners[3], -1, 1))

                handles.add(Shape2dSizeHandle(shape, (corners[0] + corners[1]) / 2.0, 0, -1))
                handles.add(Shape2dSizeHandle(shape, (corners[1] + corners[2]) / 2.0, 1, 0))
                handles.add(Shape2dSizeHandle(shape, (corners[2] + corners[3]) / 2.0, 0, 1))
                handles.add(Shape2dSizeHandle(shape, (corners[3] + corners[0]) / 2.0, -1, 0))
            }
            if (!shape.locks.rotation.value) {
                handles.add(RotationHandle(shape,
                        (corners[0] + corners[1]) / 2.0 +
                                (corners[1] - corners[2]).normalise()
                                        * Dimension(GlassCanvas.ROTATE_DISTANCE / scale)))
            }

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

    fun flip(x: Boolean, y: Boolean) {
        page.document.history.beginBatch()
        if (x) {
            selection.forEach { shape ->
                page.document.history.makeChange(ChangeExpressions(listOf(shape.transform.flipX to (!shape.transform.flipX.value).toFormula())))
            }
        }
        if (y) {
            selection.forEach { shape ->
                page.document.history.makeChange(ChangeExpressions(listOf(shape.transform.flipY to (!shape.transform.flipY.value).toFormula())))
            }
        }
        page.document.history.endBatch()
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

        val maxSnap = 10 / scale

        var bestDeltaX = delta.x
        var bestScoreX = Double.MAX_VALUE

        var bestDeltaY = delta.y
        var bestScoreY = Double.MAX_VALUE

        fun maybeAdjustEither(diff: Dimension2) {
            val scoreX = Math.abs(diff.x.inDefaultUnits)
            if (scoreX < maxSnap && scoreX < bestScoreX) {
                bestScoreX = scoreX
                bestDeltaX = delta.x + diff.x
            }
            val scoreY = Math.abs(diff.y.inDefaultUnits)
            if (scoreY < maxSnap && scoreY < bestScoreY) {
                bestScoreY = scoreY
                bestDeltaY = delta.y + diff.y
            }
        }

        // TODO Add extra snap types, such as a Grid, Rulers etc.

        shape.snapPoints.forEach { sp ->
            val pageSP = shape.fromLocalToPage.value * sp.point.value + delta

            parent.children.forEach { child ->
                if (child !in selection) {
                    child.snapPoints.forEach { childSP ->
                        val pageChildSP = child.fromLocalToPage.value * childSP.point.value
                        maybeAdjustEither(pageChildSP - pageSP)
                    }
                }
            }
        }

        return Dimension2(bestDeltaX, bestDeltaY)
    }

    companion object {

        val HANDLE_SIZE = 3.0 // Half width (or height) of handles excluding the stroke.
        val HANDLE_NEAR = HANDLE_SIZE + 2.0 // The size when testing if the mouse is at the handle
        val LINE_NEAR = Dimension(4.0) // The distance away from a line, and still be able to select it.


        fun connectFormula(pagePoint: Dimension2, shape: Shape, scale: Double): String? {
            return connectData(pagePoint, shape, scale).first
        }

        fun connectData(pagePoint: Dimension2, shape: Shape, scale: Double): Triple<String?, ConnectionPoint?, Shape?> {

            // Look for connection points
            shape.page().findNearestConnectionPoint(pagePoint, shape)?.let { (connectionPoint, distance) ->
                if (distance < HANDLE_NEAR / scale) {
                    return Triple(connectionPoint.connectToFormula(), connectionPoint, null)
                }
            }

            // Look for geometries that can be connected to.
            shape.page().findNearestConnectionGeometry(pagePoint, shape)?.let { (shape, distance, along) ->
                if (distance < HANDLE_NEAR / scale) {
                    return Triple(shape.geometry.connectAlongFormula(along), null, shape)
                }
            }

            return Triple(null, null, null)

        }
    }
}
