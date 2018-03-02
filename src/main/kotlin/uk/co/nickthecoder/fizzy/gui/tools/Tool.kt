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

abstract class Tool(val glassCanvas: GlassCanvas) {

    abstract fun onMouseClick(event: MouseEvent)
    abstract fun onMousePressed(event: MouseEvent)
    abstract fun onMouseReleased(event: MouseEvent)
    abstract fun onDragDetected(event: MouseEvent)
    abstract fun onMouseDragged(event: MouseEvent)

}
