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
package uk.co.nickthecoder.fizzy.gui.tools

import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape2d
import uk.co.nickthecoder.fizzy.model.history.MoveShapes


class DragSelection(glassCanvas: GlassCanvas, var previousPoint: Dimension2)
    : Tool(glassCanvas) {

    val document = glassCanvas.page.document

    init {
        document.history.beginBatch()
    }

    override fun onMouseClick(event: MouseEvent) {

    }

    override fun onDragDetected(event: MouseEvent) {}

    override fun onMouseDragged(event: MouseEvent) {
        val now = glassCanvas.toDimension2(event)
        val delta = now - previousPoint

        document.history.makeChange(MoveShapes(document.selection, delta))

        previousPoint = now
        event.consume()
    }

    fun move(shape: Shape, delta: Dimension2) {
        if (shape is Shape2d) {
            val oldPin = shape.transform.pin.value
            val newPin = oldPin + delta
            shape.transform.pin.expression = newPin.toString()
        }
    }

    override fun onMousePressed(event: MouseEvent) {}

    override fun onMouseReleased(event: MouseEvent) {
        document.history.endBatch()
        glassCanvas.tool = DragCompleted(glassCanvas)
        event.consume()
    }

}
