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
import uk.co.nickthecoder.fizzy.gui.handle.Handle
import uk.co.nickthecoder.fizzy.model.Dimension2

class DragHandle(glassCanvas: GlassCanvas, val handle: Handle, startPosition: Dimension2)
    : Tool(glassCanvas) {

    val offset = startPosition - handle.position

    init {
        glassCanvas.page.document.history.beginBatch()
        handle.beginDrag(startPosition)
    }

    override fun onMouseDragged(event: MouseEvent) {
        handle.dragTo(glassCanvas.toPage(event) - offset, glassCanvas.isConstrain(event))
        glassCanvas.dirty = true // TODO REMOVE when handles are implemented correctly.
    }

    override fun onMouseReleased(event: MouseEvent) {
        glassCanvas.tool = DragCompleted(glassCanvas)
        glassCanvas.page.document.history.endBatch()
    }
}