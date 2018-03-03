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
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.history.CreateShape

class StampShape1dTool(glassCanvas: GlassCanvas, val masterShape: Shape1d)
    : Tool(glassCanvas) {

    var start: Dimension2? = null
    var newShape: Shape1d? = null

    override fun onMousePressed(event: MouseEvent) {
        start = glassCanvas.toPage(event)
        glassCanvas.page.document.history.beginBatch()
    }

    override fun onDragDetected(event: MouseEvent) {
        val newShape = masterShape.copyInto(glassCanvas.page) as Shape1d
        newShape.start.formula = (start ?: Dimension2.ZERO_mm).toFormula()
        newShape.start.formula = glassCanvas.toPage(event).toFormula()

        this.newShape = newShape

        glassCanvas.page.document.history.makeChange(
                CreateShape(newShape, glassCanvas.page)
        )
    }

    override fun onMouseDragged(event: MouseEvent) {
        newShape?.let {
            // Note, we don't need to change the end point using a Change, because the Batch will only be completed
            // when the drag is completed. At which point the end point is set correctly.
            // glassCanvas.page.document.history.makeChange(ChangeExpression(it.end, glassCanvas.toPage(event).toFormula()))
            it.end.formula = glassCanvas.toPage(event).toFormula()
        }
    }

    override fun onMouseReleased(event: MouseEvent) {
        newShape = null
        glassCanvas.page.document.history.endBatch()
        // glassCanvas.tool = DragCompleted(glassCanvas)
    }
}
