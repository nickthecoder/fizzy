package uk.co.nickthecoder.fizzy.gui.tools

import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.gui.handle.Handle
import uk.co.nickthecoder.fizzy.model.Dimension2

class DragHandle(glassCanvas: GlassCanvas, val handle: Handle, startPosition: Dimension2)
    : Tool(glassCanvas) {

    val offset = startPosition - handle.position

    init {
        glassCanvas.page.document.history.beginBatch()
    }
    override fun onMouseDragged(event: MouseEvent) {
        handle.dragTo(glassCanvas.toPage(event) - offset)
        glassCanvas.dirty = true // TODO REMOVE when handles are implemented correctly.
    }

    override fun onMouseReleased(event: MouseEvent) {
        glassCanvas.tool = DragCompleted(glassCanvas)
        glassCanvas.page.document.history.endBatch()
    }
}