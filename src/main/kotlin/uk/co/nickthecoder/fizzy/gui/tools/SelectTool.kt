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

class SelectTool(glassCanvas: GlassCanvas)
    : Tool(glassCanvas) {

    val selection = glassCanvas.page.document.selection

    var mousePressedPoint = Dimension2.ZERO_mm

    override fun onMouseClick(event: MouseEvent) {

        val shapes = glassCanvas.page.findShapesAt(glassCanvas.toPage(event))
        if (shapes.isEmpty()) {
            if (!glassCanvas.isAdjust(event)) {
                selection.clear()
            }
        } else {
            val latest = selection.lastOrNull()
            val shape: Shape?
            if (shapes.size > 1 && shapes.contains(latest) && glassCanvas.isConstrain(event)) {
                val i = shapes.indexOf(latest)
                shape = if (i == 0) shapes.last() else shapes[i - 1]
            } else {
                shape = shapes.last()
            }

            if (glassCanvas.isAdjust(event)) {
                if (selection.contains(shape)) {
                    selection.remove(shape)
                } else {
                    selection.add(shape)
                }
            } else {
                selection.clear()
                selection.add(shape)
            }
        }
        event.consume()
    }

    override fun onMousePressed(event: MouseEvent) {
        mousePressedPoint = glassCanvas.toPage(event)
    }

    override fun onMouseDragged(event: MouseEvent) {}

    override fun onDragDetected(event: MouseEvent) {
        val shape = glassCanvas.page.findShapeAt(glassCanvas.toPage(event))
        if (shape == null) {
            glassCanvas.tool = DragBoundingBox(glassCanvas, event, mousePressedPoint)
            glassCanvas.tool.onMouseDragged(event)
        } else {
            if (!selection.contains(shape)) {
                selection.clear()
                selection.add(shape)
            }
            glassCanvas.tool = DragSelection(glassCanvas, mousePressedPoint)
            glassCanvas.tool.onMouseDragged(event)
        }
        event.consume()
    }

    override fun onMouseReleased(event: MouseEvent) {}
}
