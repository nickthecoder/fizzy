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
package uk.co.nickthecoder.fizzy.controller.tools

import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.controller.handle.Handle
import uk.co.nickthecoder.fizzy.model.Dimension2

class DragHandleTool(controller: Controller, val handle: Handle, startPosition: Dimension2)
    : Tool(controller) {

    val offset = startPosition - handle.position

    init {
        controller.page.document.history.beginBatch()
        handle.beginDrag(startPosition)
    }

    override fun onMouseDragged(event: CMouseEvent) {
        handle.dragTo(event, event.point - offset)
    }

    override fun onMouseReleased(event: CMouseEvent) {
        controller.tool = SelectTool(controller)
        controller.page.document.history.endBatch()
    }
}