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
import uk.co.nickthecoder.fizzy.gui.GlassCanvas
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.view.DrawContext

/**
 * Drags a rectangle, and selects all shapes wholly contained within it.
 */
class BoundingBoxTool(controller: Controller, event: CMouseEvent, val startPoint: Dimension2)
    : Tool(controller) {

    var endPoint = startPoint

    val selection = controller.page.document.selection

    init {
        if (!event.isAdjust) {
            selection.clear()
        }
    }

    override fun onMouseDragged(event: CMouseEvent) {
        endPoint = event.point
        controller.dirty.value++
    }

    override fun onMouseReleased(event: CMouseEvent) {
        controller.page.children.filter { controller.isWithin(it, startPoint, endPoint) }.forEach {
            selection.add(it)
        }
        controller.tool = SelectTool(controller)
        controller.dirty.value++
    }

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.lineColor(GlassCanvas.BOUNDING_STROKE)
            dc.fillColor(GlassCanvas.BOUNDING_FILL)
            dc.lineWidth(2.0 / controller.scale)
            dc.lineDashes(5.0 / controller.scale)
            dc.rectangle(true, true, startPoint, endPoint)
        }
    }
}
