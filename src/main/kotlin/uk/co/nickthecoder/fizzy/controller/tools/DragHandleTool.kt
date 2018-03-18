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
import uk.co.nickthecoder.fizzy.controller.handle.Shape1dHandle
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.NO_SHAPE

open class DragHandleTool(
        controller: Controller,
        val handle: Handle,
        startPosition: Dimension2,
        val nextTool: Tool = SelectTool(controller))
    : Tool(controller) {

    val offset = startPosition - handle.position

    init {
        controller.page.document.history.beginBatch()
        handle.beginDrag(startPosition)
    }

    override fun beginTool() {
        if (handle is Shape1dHandle) {
            controller.showConnectionPoints.value = true
        }
    }

    override fun endTool(replacement: Tool) {
        if (handle is Shape1dHandle) {
            controller.showConnectionPoints.value = false
            controller.highlightShape.value = NO_SHAPE
        }
    }

    override fun onMouseDragged(event: CMouseEvent) {
        handle.dragTo(event, event.point - offset)
    }

    override fun onMouseReleased(event: CMouseEvent) {
        controller.tool = nextTool
        controller.page.document.history.endBatch()
    }
}