package uk.co.nickthecoder.fizzy.gui

import javafx.scene.Node
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.fizzy.model.Page

/**
 * Contains a GlassCanvas on top of a DrawingCanvas.
 * Later, this may also contain other GUI elements such as rulers.
 *
 */
class DrawingArea(page: Page) : BuildableNode {

    var page: Page = page
        set(v) {
            assert(page.document === v.document)
            field = v
            glassCanvas.page = v
            drawingCanvas.page = v
        }

    val glassCanvas = GlassCanvas(page)

    val drawingCanvas = DrawingCanvas(page)

    private val stackPane = StackPane()

    override fun build(): Node {
        assert(stackPane.children.size == 0)
        stackPane.children.add(drawingCanvas.build())
        stackPane.children.add(glassCanvas.build())

        return stackPane
    }

}
