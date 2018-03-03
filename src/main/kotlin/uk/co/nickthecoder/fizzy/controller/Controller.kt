package uk.co.nickthecoder.fizzy.controller

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.controller.handle.Handle
import uk.co.nickthecoder.fizzy.controller.handle.RotationHandle
import uk.co.nickthecoder.fizzy.controller.handle.Shape2dSizeHandle
import uk.co.nickthecoder.fizzy.controller.handle.ShapeHandle
import uk.co.nickthecoder.fizzy.controller.tools.SelectTool
import uk.co.nickthecoder.fizzy.controller.tools.Tool
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.*
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

    val handles = mutableListOf<Handle>()

    /**
     * The minimum distance away from a line for it to be considered close enough to select it.
     * This should be scaled with
     */
    val minDistance: Dimension
        get() = Dimension(4.0) / scale

    /**
     * Listen to the currently selected shapes. When they change, we need to draw their handles and bounding box etc.
     */
    val shapeListener = object : ChangeListener<Shape> {
        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            if (page.document.selection.contains(item)) {
                createShapeHandles(item)
                dirty.value ++
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

    init {
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
            handles.add(ShapeHandle(shape, ends[0]))
            handles.add(ShapeHandle(shape, ends[1]))
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

    fun shape1dEnds(shape: Shape1d): Array<Dimension2> {
        return arrayOf<Dimension2>(
                shape.parent.fromLocalToPage.value * shape.start.value,
                shape.parent.fromLocalToPage.value * shape.end.value
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

}
