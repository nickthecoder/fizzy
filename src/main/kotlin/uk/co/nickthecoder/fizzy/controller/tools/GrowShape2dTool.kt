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
import uk.co.nickthecoder.fizzy.model.Color
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape2d
import uk.co.nickthecoder.fizzy.model.history.CreateShape

/**
 * Adds a shape to the document using a click and drag to scale it to the desired size.
 */
class GrowShape2dTool(
        controller: Controller,
        val masterShape: Shape2d,
        val strokeColor: Color? = null,
        val fillColor: Color? = null)
    : Tool(controller) {

    override val cursor: ToolCursor = ToolCursor.GROW

    var startPoint: Dimension2? = null

    var newShape: Shape2d? = null

    val aspectRatio = masterShape.size.value.aspectRatio()

    init {
        controller.page.document.selection.clear()
    }

    override fun onMousePressed(event: CMouseEvent) {
        controller.page.document.history.beginBatch()
        startPoint = event.point
    }

    override fun onDragDetected(event: CMouseEvent) {
        startPoint?.let {
            val newShape = masterShape.copyInto(controller.page, true)
            newShape.size.formula = Dimension2.ZERO_mm.toFormula()
            newShape.transform.pin.formula = it.toFormula()

            strokeColor?.let { newShape.strokeColor.formula = it.toFormula() }
            fillColor?.let { newShape.fillColor.formula = it.toFormula() }

            controller.page.document.history.makeChange(
                    CreateShape(newShape, controller.page)
            )
            this.newShape = newShape
        }
    }

    override fun onMouseDragged(event: CMouseEvent) {
        startPoint?.let { startPoint ->
            newShape?.let { newShape ->

                var newSize = (event.point - startPoint)
                if (event.isConstrain) {
                    newSize = newSize.keepAspectRatio(aspectRatio)
                }
                val ratio = newShape.transform.locPin.value.ratio(newShape.size.value)

                newShape.transform.pin.formula = (startPoint + newSize * ratio).toFormula()
                newShape.size.formula = newSize.toFormula()
            }
        }
    }

    override fun onMouseReleased(event: CMouseEvent) {
        controller.page.document.history.endBatch()
        newShape?.let {
            it.document().selection.clear()
            it.document().selection.add(it)
        }
        newShape = null
        startPoint = null
        controller.tool = SelectTool(controller)
    }
}
