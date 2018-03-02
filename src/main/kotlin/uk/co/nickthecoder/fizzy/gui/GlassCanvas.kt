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
package uk.co.nickthecoder.fizzy.gui

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.gui.tools.SelectTool
import uk.co.nickthecoder.fizzy.gui.tools.Tool
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.runLater

/**
 * A Canvas above the drawing, where things such as bounding boxes and control handles are drawn.
 * By drawing these onto a different [Canvas], the [Canvas] displaying the the document does not need
 * to be redrawn when the selection changes, or while dragging a bounding box.
 */
class GlassCanvas(var page: Page, val drawingArea: DrawingArea) {

    var dirty = false
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    runLater { draw() }
                }
            }
        }
    // TODO We need to fit the canvas to the correct size
    val canvas = Canvas(1000.0, 800.0)

    val dc = CanvasContext(canvas)

    private var selectionMargin = 10.0

    private val handles = mutableListOf<Handle>()

    var tool: Tool = SelectTool(this)

    fun build(): Node = canvas

    /**
     * Listen to the currently selected shapes. When they change, we need to draw their handles and bounding box etc.
     */
    val shapeListener = object : ChangeListener<Shape> {
        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            dirty = true
            if (page.document.selection.contains(item)) {
                addShapeHandles(item)
            }
        }
    }

    /**
     * When the selection changes, we need to draw, to update the bounding boxes and handles on display.
     */
    val selectionListener = object : CollectionListener<Shape> {

        override fun added(collection: FCollection<Shape>, item: Shape) {
            item.changeListeners.add(shapeListener)
            addShapeHandles(item)
            dirty = true
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            item.changeListeners.remove(shapeListener)
            removeShapeHandles(item)
            dirty = true
        }
    }

    private var previousPoint = Dimension2.ZERO_mm

    private var dragging = false

    init {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED) { onMousePressed(it) }
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        canvas.addEventHandler(MouseEvent.DRAG_DETECTED) { onDragDetected(it) }
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED) { onMouseDragged(it) }
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouseReleased(it) }

        (page.document.selection.listeners.add(selectionListener))
        page.document.selection.forEach {
            addShapeHandles(it)
        }
        runLater {
            draw()
        }
    }


    fun onMousePressed(event: MouseEvent) {
        if (event.button == MouseButton.SECONDARY || event.isMiddleButtonDown || event.isMetaDown || event.isAltDown || isConstrain(event)) {
            dragging = true
            previousPoint = toPage(event)
            event.consume()
        } else if (event.clickCount == 2) {
            drawingArea.zoomOn(if (isAdjust(event)) 1.0 / 1.4 else 1.4, event.x, event.y)
        } else {
            tool.onMousePressed(event)
        }
    }

    fun onMouseReleased(event: MouseEvent) {
        if (dragging) {
            event.consume()
        } else {
            tool.onMouseReleased(event)
        }
    }

    fun onDragDetected(event: MouseEvent) {
        if (dragging) {
            event.consume()
        } else {
            tool.onDragDetected(event)
        }
    }

    fun onMouseDragged(event: MouseEvent) {
        if (dragging) {
            val newPoint = toPage(event)
            drawingArea.panBy(newPoint - previousPoint)
            previousPoint = toPage(event)
            event.consume()
        } else {
            tool.onMouseDragged(event)
        }
    }

    fun onMouseClicked(event: MouseEvent) {
        if (dragging) {
            dragging = false
            event.consume()
        } else {
            tool.onMouseClick(event)
        }
    }

    fun draw() {
        dc.clear()
        dc.use {
            dc.scale(drawingArea.scale)
            dc.translate(drawingArea.pan)

            page.document.selection.forEach { shape ->
                drawBoundingBox(shape)
            }
            dc.use {
                beginHandle()
                handles.forEach { handle ->
                    handle.draw(dc)
                }
            }
            tool.draw(dc)
        }
        dirty = false
    }

    fun drawBoundingBox(shape: Shape) {
        dc.use {

            if (shape is RealShape) {
                beginSelection()
                val corners = shapeCorners(shape)
                dc.polygon(true, false, *corners)
                val r1 = (corners[0] + corners[1]) / 2.0
                val r2 = r1 + (corners[1] - corners[2]).normalise() * Dimension(ROTATE_DISTANCE)
                dc.beginPath()
                dc.moveTo(r1)
                dc.lineTo(r2)
                dc.endPath(true, false)
            } else if (shape is ShapeGroup) {

            }
        }
    }

    fun use(gc: GraphicsContext, action: () -> Unit) {
        gc.save()
        action()
        gc.restore()
    }

    fun beginSelection() {
        dc.lineColor(Color.web("#72c2e9"))
        dc.lineWidth(2.0)
        dc.lineDashes(5.0, 5.0)
    }

    fun beginHandle() {
        dc.lineColor(HANDLE_STROKE)
        dc.fillColor(HANDLE_FILL)
        dc.lineWidth(1.0)
    }

    fun addShapeHandles(shape: Shape) {
        removeShapeHandles(shape)
        if (shape is RealShape) {
            val corners = shapeCorners(shape)
            handles.add(ShapeHandle(shape, corners[0]))
            handles.add(ShapeHandle(shape, corners[1]))
            handles.add(ShapeHandle(shape, corners[2]))
            handles.add(ShapeHandle(shape, corners[3]))

            handles.add(ShapeHandle(shape, (corners[0] + corners[1]) / 2.0))
            handles.add(ShapeHandle(shape, (corners[1] + corners[2]) / 2.0))
            handles.add(ShapeHandle(shape, (corners[2] + corners[3]) / 2.0))
            handles.add(ShapeHandle(shape, (corners[3] + corners[0]) / 2.0))

            handles.add(RotationHandle(shape,
                    (corners[0] + corners[1]) / 2.0 +
                            (corners[1] - corners[2]).normalise()
                                    * Dimension(ROTATE_DISTANCE)))
        }
    }

    fun removeShapeHandles(shape: Shape) {
        handles.removeIf { it.isFor(shape) }
    }

    fun shapeCorners(shape: RealShape): Array<Dimension2> {
        return arrayOf<Dimension2>(
                shape.fromLocalToPage.value * Dimension2.ZERO_mm,
                shape.fromLocalToPage.value * (shape.size.value * Vector2(1.0, 0.0)),
                shape.fromLocalToPage.value * (shape.size.value * Vector2(1.0, 1.0)),
                shape.fromLocalToPage.value * (shape.size.value * Vector2(0.0, 1.0))
        )
    }

    fun isWithin(shape: Shape, a: Dimension2, b: Dimension2): Boolean {
        if (shape is RealShape) {
            shapeCorners(shape).forEach {
                if (((it.x < a.x) xor (it.x > b.x)) || ((it.y < a.y) xor (it.y > b.y))) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun toPage(event: MouseEvent) = drawingArea.toPage(event)


    /**
     * Is this mouse event an adjustment? (When the shift key is down).
     * Used to add/remove items to/from the selection rather than replace the selection with a single item.
     */
    fun isAdjust(event: MouseEvent) = event.isShiftDown

    /**
     * Is this mouse event constrained? (When the control key is down).
     * The SelectTool uses it to select a shape hidden under a another shape.
     * The rezise tools will use it keep the same aspect ratio.
     */
    fun isConstrain(event: MouseEvent) = event.isControlDown

    companion object {
        val ROTATE_DISTANCE = 40.0

        val BLUE_BASE = "#72c2e9"
        val HANDLE_STROKE = Color.web(BLUE_BASE).darker()
        val HANDLE_FILL = Color.web(BLUE_BASE).brighter()
        val BOUNDING_STROKE = Color.web(BLUE_BASE)
        val BOUNDING_FILL = Color.web(BLUE_BASE, 0.3).brighter()
    }
}
