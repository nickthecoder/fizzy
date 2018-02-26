package uk.co.nickthecoder.fizzy.gui

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.fizzy.model.Page

/**
 * A Canvas above the drawing, where things such as bounding boxes and control handles are drawn.
 * By drawing these onto a different [Canvas], the [Canvas] displaying the the document does not need
 * to be redrawn when the selection changes, or while dragging a bounding box.
 */
class GlassCanvas(var page: Page) {

    // TODO We need to fit the canvas to the correct size
    val canvas = Canvas(1000.0, 800.0)

    fun build(): Node = canvas
}
