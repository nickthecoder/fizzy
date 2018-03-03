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
        endPoint = glassCanvas.toPage(event)
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
            dc.lineColor(GlassCanvas.BOUNDING_STROKE)
            dc.fillColor(GlassCanvas.BOUNDING_FILL)
            dc.lineWidth(2.0 / glassCanvas.drawingArea.scale)
            dc.lineDashes(5.0 / glassCanvas.drawingArea.scale)
            dc.rectangle(true, true, startPoint, endPoint)
        }
    }
}
