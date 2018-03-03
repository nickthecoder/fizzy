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
import uk.co.nickthecoder.fizzy.model.Shape2d
import uk.co.nickthecoder.fizzy.model.history.CreateShape

class StampShape2dTool(glassCanvas: GlassCanvas, val masterShape: Shape2d)
    : Tool(glassCanvas) {

    override fun onMouseClick(event: MouseEvent) {
        val newShape = masterShape.copyInto(glassCanvas.page)
        newShape.transform.pin.formula = glassCanvas.toPage(event).toFormula()

        glassCanvas.page.document.history.makeChange(
                CreateShape(newShape, glassCanvas.page)
        )
    }
}
