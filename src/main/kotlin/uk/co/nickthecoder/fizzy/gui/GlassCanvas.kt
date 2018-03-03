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
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.runLater

/**
 * A Canvas above the drawing, where things such as bounding boxes and control handles are drawn.
 * By drawing these onto a different [Canvas], the [Canvas] displaying the the document does not need
 * to be redrawn when the selection changes, or while dragging a bounding box.
 */
class GlassCanvas(val page: Page, val drawingArea: DrawingArea) {

    // TODO We need to fit the canvas to the correct size
    val canvas = Canvas(1000.0, 800.0)

    val dc = CanvasContext(canvas)

    private var selectionMargin = 10.0


    fun build(): Node = canvas

    /**
     * Listen to the currently selected shapes. When they change, we need to draw their handles and bounding box etc.
     */
    val shapeListener = object : ChangeListener<Shape> {
        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            dirty = true
        }
    }

    /**
     * When the selection changes, we need to draw, to update the bounding boxes and handles on display.
     */
    val selectionListener = object : CollectionListener<Shape> {

        override fun added(collection: FCollection<Shape>, item: Shape) {
            item.changeListeners.add(shapeListener)
            dirty = true
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            item.changeListeners.remove(shapeListener)
            dirty = true
        }
    }

    private var previousPoint = Dimension2.ZERO_mm

    private var panning = false

    /**
     * Use to ignore the spurious onMouseClicked event at the end of a drag.
     * It seems that PRESS, MOVE RELEASE also produces an onMouseClicked event at the end.
     * IMHO, this is NOT a click, and so I want to ignore it.
     * Set inside [onDragDetected], reset inside [onMousePressed] and [onMouseClicked].
     */
    private var dragging = false

    var dirty: Boolean = false
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    runLater { draw() }
                }
            }
        }

    private val dirtyListener = object : PropListener {
        override fun dirty(prop: Prop<*>) {
            dirty = true
        }
    }

    init {
        drawingArea.controller.dirty.propListeners.add(dirtyListener)

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED) { onMousePressed(it) }
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        canvas.addEventHandler(MouseEvent.DRAG_DETECTED) { onDragDetected(it) }
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED) { onMouseDragged(it) }
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouseReleased(it) }

        page.document.selection.listeners.add(selectionListener)
        runLater {
            draw()
        }
    }

    fun toPage(event: MouseEvent) = drawingArea.toPage(event)

    fun convertEvent(event: MouseEvent) = CMouseEvent(
            toPage(event),
            event.button.ordinal,
            event.isShiftDown,
            event.isControlDown,
            drawingArea.scale)

    fun onMousePressed(event: MouseEvent) {
        dragging = false

        if (event.button == MouseButton.SECONDARY || event.isMiddleButtonDown || event.isMetaDown || event.isAltDown) {
            panning = true
            previousPoint = toPage(event)
        } else if (event.clickCount == 2) {
            drawingArea.zoomOn(if (convertEvent(event).isAdjust) 1.0 / 1.4 else 1.4, event.x, event.y)
        } else {
            drawingArea.controller.onMousePressed(convertEvent(event))
        }
        event.consume()
    }

    fun onMouseReleased(event: MouseEvent) {
        if (!panning) {
            drawingArea.controller.onMouseReleased(convertEvent(event))
        }
        event.consume()
    }

    fun onDragDetected(event: MouseEvent) {
        dragging = true

        if (panning) {
            event.consume()
        } else {
            drawingArea.controller.onDragDetected(convertEvent(event))
        }
        event.consume()
    }

    fun onMouseDragged(event: MouseEvent) {
        if (panning) {
            val newPoint = toPage(event)
            drawingArea.panBy(newPoint - previousPoint)
            previousPoint = toPage(event)
        } else {
            drawingArea.controller.onMouseDragged(convertEvent(event))
        }
        event.consume()
    }

    fun onMouseClicked(event: MouseEvent) {
        // Ignore the spurious onMouseClicked event that JavaFX spits out at the end of a drag. Grr.
        if (dragging) {
            event.consume()
            dragging = false
            return
        }

        if (panning) {
            panning = false
        } else {
            drawingArea.controller.onMouseClicked(convertEvent(event))
        }
        event.consume()
    }

    fun draw() {
        dc.clear()
        dc.use {
            dc.scale(drawingArea.scale)
            dc.translate(drawingArea.pan)

            page.document.selection.forEach { shape ->
                drawBoundingBox(shape)
            }
            drawingArea.controller.handles.forEach { handle ->
                dc.use {
                    dc.translate(handle.position)
                    beginHandle()
                    handle.draw(dc)
                }
            }
            drawingArea.controller.tool.draw(dc)
        }
        dirty = false
    }

    fun drawBoundingBox(shape: Shape) {
        dc.use {

            if (shape is RealShape) {
                beginSelection()
                val corners = drawingArea.controller.shapeCorners(shape)
                dc.polygon(true, false, *corners)
                if (shape is Shape2d) {
                    val r1 = (corners[0] + corners[1]) / 2.0
                    val r2 = r1 + (corners[1] - corners[2]).normalise() * Dimension(ROTATE_DISTANCE)
                    dc.beginPath()
                    dc.moveTo(r1)
                    dc.lineTo(r2)
                    dc.endPath(true, false)
                }
            } else if (shape is ShapeGroup) {

            }
        }
    }

    fun beginSelection() {
        dc.lineColor(BLUE_BASE)
        dc.lineWidth(2.0 / drawingArea.scale)
        dc.lineDashes(5.0 / drawingArea.scale)
    }

    fun beginHandle() {
        dc.lineColor(HANDLE_STROKE)
        dc.fillColor(HANDLE_FILL)
        dc.scale(1.0 / drawingArea.scale)
        dc.lineWidth(1.0)
    }


    companion object {
        val ROTATE_DISTANCE = 40.0

        val BLUE_BASE_STRING = "#72c2e9"
        val BLUE_BASE = Color.web(BLUE_BASE_STRING)
        val HANDLE_STROKE = BLUE_BASE.darker()
        val HANDLE_FILL = BLUE_BASE.brighter()
        val BOUNDING_STROKE = BLUE_BASE
        val BOUNDING_FILL = BLUE_BASE.transparent(0.3).brighter()
    }
}
