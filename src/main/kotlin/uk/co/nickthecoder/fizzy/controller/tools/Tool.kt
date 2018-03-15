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
import uk.co.nickthecoder.fizzy.view.DrawContext

enum class ToolCursor {
    DEFAULT, GROW, STAMP, MOVE, DELETE
}

abstract class Tool(val controller: Controller) {

    open val cursor = ToolCursor.DEFAULT

    open fun onMouseClicked(event: CMouseEvent) {}
    open fun onMousePressed(event: CMouseEvent) {}
    open fun onMouseReleased(event: CMouseEvent) {}
    open fun onDragDetected(event: CMouseEvent) {}
    open fun onMouseDragged(event: CMouseEvent) {}
    open fun onMouseMoved(event: CMouseEvent) {}

    open fun onContextMenu(event: CMouseEvent): List<Pair<String, () -> Unit>> = emptyList()

    open fun draw(dc: DrawContext) {}

    /**
     * Called when the tool is the current tool of the [Controller]
     */
    open fun beginTool() {}

    /**
     * Called when a tool is no longer active.
     */
    open fun endTool(replacement: Tool) {}

}
