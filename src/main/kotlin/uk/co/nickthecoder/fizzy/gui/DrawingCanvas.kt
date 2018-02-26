package uk.co.nickthecoder.fizzy.gui

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.view.PageView

/**
 *
 */
class DrawingCanvas(page: Page) : BuildableNode {

    // TODO We need to fit the canvas to the correct size
    val canvas = Canvas(1000.0, 800.0)

    private var pageView = PageView(page, CanvasContext(canvas))

    var page: Page = page
        set(v) {
            field = v
            pageView = PageView(v, CanvasContext(canvas))
            pageView.draw()
        }

    override fun build(): Node {
        pageView.draw()
        return canvas
    }
}
