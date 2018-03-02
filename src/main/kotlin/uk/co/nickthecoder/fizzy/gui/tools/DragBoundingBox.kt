package uk.co.nickthecoder.fizzy.gui.tools

import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.Color
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.view.DrawContext

class DragBoundingBox(glassCanvas: GlassCanvas, event: MouseEvent, val startPoint: Dimension2)
    : Tool(glassCanvas) {

    var endPoint = startPoint

    val selection = glassCanvas.page.document.selection

    init {
        if (!glassCanvas.isAdjust(event)) {
            selection.clear()
        }
    }

    override fun onMouseDragged(event: MouseEvent) {
        endPoint = glassCanvas.toDimension2(event)
        glassCanvas.dirty = true
    }

    override fun onMouseReleased(event: MouseEvent) {
        glassCanvas.page.children.filter { glassCanvas.isWithin(it, startPoint, endPoint) }.forEach {
            selection.add(it)
        }
        glassCanvas.tool = DragCompleted(glassCanvas)
        glassCanvas.dirty = true
    }

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.lineColor(Color.web("#72c2e9"))
            dc.fillColor(Color.web("#72c2e9", 0.3).brighter())
            dc.lineWidth(2.0)
            dc.lineDashes(5.0)
            dc.rectangle(true, true, startPoint, endPoint)
        }
    }
}
