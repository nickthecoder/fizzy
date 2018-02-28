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
