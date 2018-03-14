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
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.runLater
import uk.co.nickthecoder.fizzy.view.PageView
import uk.co.nickthecoder.fizzy.view.ShapeView

/**
 *
 */
class DrawingCanvas(page: Page, val singleShape: Shape?, val drawingArea: DrawingArea)
    : BuildableNode, ChangeListener<Page> {

    val canvas = Canvas(100.0, 100.0)

    private var pageView = if (singleShape == null) PageView(page, CanvasContext(canvas)) else ShapeView(singleShape, CanvasContext(canvas))

    var dirty = false
        set(v) {
            if (field != v) {
                field = v

                if (v) {
                    runLater {
                        draw()
                    }
                }
            }
        }

    var page: Page = page
        set(v) {
            field = v
            pageView = PageView(v, CanvasContext(canvas))
            pageView.draw()
        }

    init {
        page.changeListeners.add(this)
    }

    override fun changed(item: Page, changeType: ChangeType, obj: Any?) {
        dirty = true
    }

    override fun build(): Node {
        pageView.draw()
        return canvas
    }

    fun draw() {
        pageView.dc.clear()
        pageView.dc.use {
            pageView.dc.scale(drawingArea.scale)
            pageView.dc.translate(drawingArea.pan)
            pageView.draw()
            dirty = false
        }
    }
}
