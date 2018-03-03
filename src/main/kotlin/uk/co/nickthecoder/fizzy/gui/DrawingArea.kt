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
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Page

/**
 * Contains a GlassCanvas on top of a DrawingCanvas.
 * Later, this may also contain other GUI elements such as rulers.
 *
 */
class DrawingArea(val page: Page)
    : BuildableNode {

    val controller = Controller(page)

    val glassCanvas = GlassCanvas(page, this)

    val drawingCanvas = DrawingCanvas(page, this)

    var scale: Double = 1.0

    var pan: Dimension2 = Dimension2.ZERO_mm

    private val stackPane = StackPane()


    override fun build(): Node {
        assert(stackPane.children.size == 0)
        stackPane.children.add(drawingCanvas.build())
        stackPane.children.add(glassCanvas.build())

        return stackPane
    }

    fun panBy(by: Dimension2) {
        pan += by
        controller.dirty.value ++
        drawingCanvas.dirty = true
    }

    fun zoomOn(by: Double, atX: Double, atY: Double) {
        val pagePoint = pixelsToPage(atX, atY)
        scale *= by
        pan += pixelsToPage(atX, atY) - pagePoint
        controller.dirty.value ++
        drawingCanvas.dirty = true
    }

    fun pixelsToPage(x: Double, y: Double): Dimension2 = Dimension2(
            Dimension(x / scale - pan.x.inDefaultUnits),
            Dimension(y / scale - pan.y.inDefaultUnits))

    fun toPage(event: MouseEvent) = pixelsToPage(event.x, event.y)

}
