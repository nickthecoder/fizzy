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

    val DOCUMENT_NEW = ApplicationAction("document-new", "Create a New Document", KeyCode.N, control = true)
    val DOCUMENT_OPEN = ApplicationAction("document-open", "Open a Document", KeyCode.O, control = true)
    val DOCUMENT_SAVE = ApplicationAction("document-save", "Save the Document", KeyCode.S, control = true)
    val DOCUMENT_SAVE_AS = ApplicationAction("document-save-as", "Save the Document with a Different Name", KeyCode.S, control = true, shift = true)

    val EDIT_UNDO = ApplicationAction("edit-undo", "Undo", KeyCode.Z, control = true)
    val EDIT_REDO = ApplicationAction("edit-redo", "Redo", KeyCode.Z, control = true, shift = true)

    val EDIT_FLIP_X = ApplicationAction("edit-flip-x", "Flip Horizontally", KeyCode.H, control = true)
    val EDIT_FLIP_Y = ApplicationAction("edit-flip-y", "Flip Vertically", KeyCode.J, control = true)

    val TOOL_SELECT = ApplicationAction("tool-select", "Select", KeyCode.F1)
    val TOOL_PRIMITIVE1D = ApplicationAction("tool-primitive1d", "Create Lines", KeyCode.F2)
    val TOOL_PRIMITIVE2D = ApplicationAction("tool-primitive2d", "Create Basic Shapes", KeyCode.F3)
    val TOOL_DELETE = ApplicationAction("tool-delete", "Delete", KeyCode.F4)
    val TOOL_EDIT_GEOMETRY = ApplicationAction("tool-edit-geometry", "Edit Geometry", KeyCode.F5)
    val TOOL_EDIT_TEXT = ApplicationAction("tool-edit-text", "Edit Text", KeyCode.F8)

    val DEV_DEBUG = ApplicationAction("dev-debug", "Debug", KeyCode.F1, control = true)

    val CLOSE_TAB = ApplicationAction("close-tab", "Close Tab", KeyCode.W, control = true)
}
