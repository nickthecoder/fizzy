package uk.co.nickthecoder.fizzy.gui.tools

import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.Shape2d
import uk.co.nickthecoder.fizzy.model.history.CreateShape

class StampShape2dTool(glassCanvas: GlassCanvas, val masterShape: Shape2d)
    : Tool(glassCanvas) {

    override fun onMouseClick(event: MouseEvent) {
        val newShape = masterShape.copyInto(glassCanvas.page)

        glassCanvas.page.document.history.makeChange(
                CreateShape(newShape, glassCanvas.page, glassCanvas.toPage(event))
        )
    }
}
