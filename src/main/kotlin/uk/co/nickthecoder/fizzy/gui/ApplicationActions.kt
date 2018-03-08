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
package uk.co.nickthecoder.fizzy.gui

import javafx.scene.input.KeyCode

object ApplicationActions {

    val EDIT_UNDO = ApplicationAction("edit-undo", "Undo", "Undo", KeyCode.Z, control = true)
    val EDIT_REDO = ApplicationAction("edit-redo", "Redo", "Redo", KeyCode.Z, control = true, shift = true)

    val TOOL_SELECT = ApplicationAction("tool-select", "Select", "Select Tool", KeyCode.F1)
    val TOOL_PRIMITIVE1D = ApplicationAction("tool-line", "Create Lines", "Lines", KeyCode.F2)
    val TOOL_PRIMITIVE2D = ApplicationAction("tool-line", "Create  Basic Shapes", "Basic Shapes", KeyCode.F3)
    val TOOL_DELETE = ApplicationAction("tool-delete", "Delete", "Delete Tool", KeyCode.F4)

    val DEV_DEBUG = ApplicationAction("dev-debug", "Debug", "Dumps data to the console", KeyCode.F1, control = true)
}
