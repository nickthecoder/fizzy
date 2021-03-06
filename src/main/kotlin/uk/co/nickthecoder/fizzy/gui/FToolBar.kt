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

import javafx.scene.Node
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.controller.tools.DeleteTool
import uk.co.nickthecoder.fizzy.controller.tools.EditGeometryTool
import uk.co.nickthecoder.fizzy.controller.tools.EditTextTool
import uk.co.nickthecoder.fizzy.controller.tools.SelectTool
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.PrimitiveStencil

class FToolBar(val mainWindow: MainWindow)
    : BuildableNode {

    val toolBar = ToolBar()

    val document: Document?
        get() = mainWindow.document

    val drawingArea: DrawingArea?
        get() = mainWindow.documentTab?.drawingArea

    val controller: Controller?
        get() = drawingArea?.controller

    val sh = mainWindow.shortcutHelper


    val primitives2d = arrayOf(

            ShapePickerItem("primitive-box", "Box", PrimitiveStencil.box),

            ShapePickerItem("primitive-poly3", "Triangle", PrimitiveStencil.poly3),
            ShapePickerItem("primitive-poly4", "Diamond", PrimitiveStencil.poly4),
            ShapePickerItem("primitive-poly5", "Pentagon", PrimitiveStencil.poly5),
            ShapePickerItem("primitive-poly6", "Hexagon", PrimitiveStencil.poly6),
            ShapePickerItem("primitive-poly7", "Heptagon", PrimitiveStencil.poly7),
            ShapePickerItem("primitive-poly8", "Octagon", PrimitiveStencil.poly8),

            ShapePickerItem("primitive-star3", "3 Pointed Star", PrimitiveStencil.star3),
            ShapePickerItem("primitive-star4", "4 Pointed Star", PrimitiveStencil.star4),
            ShapePickerItem("primitive-star5", "5 Pointed Star", PrimitiveStencil.star5),
            ShapePickerItem("primitive-star6", "6 Pointed Star", PrimitiveStencil.star6)
    )

    val primitives1d = arrayOf(
            ShapePickerItem("primitive-line", "Line", PrimitiveStencil.line)
    )

    val lineWidths = arrayOf(
            Dimension(0.5, Dimension.Units.mm),
            Dimension(1.0, Dimension.Units.mm),
            Dimension(2.0, Dimension.Units.mm),
            Dimension(3.0, Dimension.Units.mm),
            Dimension(4.0, Dimension.Units.mm)
    )

    val openButton = ApplicationActions.DOCUMENT_OPEN.createButton(sh) { mainWindow.documentOpen() }
    val newButton = ApplicationActions.DOCUMENT_NEW.createButton(sh) { mainWindow.documentNew() }
    val saveButton = ApplicationActions.DOCUMENT_SAVE.createButton(sh) { mainWindow.documentSave() }
    val saveAsButton = ApplicationActions.DOCUMENT_SAVE_AS.createButton(sh) { mainWindow.documentSaveAs() }

    val undoButton = ApplicationActions.EDIT_UNDO.createButton(sh) {
        document?.history?.undo()
        clearSelection()
    }
    val redoButton = ApplicationActions.EDIT_REDO.createButton(sh) {
        document?.history?.redo()
        clearSelection()
    }

    val selectToolButton = ApplicationActions.TOOL_SELECT.createButton(sh) { controller?.let { it.tool = SelectTool(it) } }
    val editGeometryToolButton = ApplicationActions.TOOL_EDIT_GEOMETRY.createButton(sh) { controller?.let { it.tool = EditGeometryTool(it) } }
    val editTextToolButton = ApplicationActions.TOOL_EDIT_TEXT.createButton(sh) { controller?.let { it.tool = EditTextTool(it) } }

    val primitive1dButton = ApplicationActions.TOOL_PRIMITIVE1D.create(sh, ShapePicker(mainWindow, primitives1d))
    val primitive2dButton = ApplicationActions.TOOL_PRIMITIVE2D.create(sh, ShapePicker(mainWindow, primitives2d))

    val deleteToolButton = ApplicationActions.TOOL_DELETE.createButton(sh) { controller?.let { it.tool = DeleteTool(it) } }

    val debugButton = ApplicationActions.DEV_DEBUG.createButton(sh) { mainWindow.debug() }

    val lineWidthPicker = LineWidthPicker(mainWindow, lineWidths)
    val strokeColorPicker = FColorPicker(mainWindow, "stroke", isStroke = true)
    val fillColorPicker = FColorPicker(mainWindow, "fill", isStroke = false)
    val strokeJoinPicker = StrokeJoinPicker(mainWindow)
    val strokeCapPicker = StrokeCapPicker(mainWindow)

    val flipXButton = ApplicationActions.EDIT_FLIP_X.createButton(sh) { controller?.flip(true, false) }
    val flipYButton = ApplicationActions.EDIT_FLIP_Y.createButton(sh) { controller?.flip(false, true) }

    fun clearSelection() {
        controller?.selection?.clear()
        controller?.handles?.clear()
        controller?.dirty?.let { it.value++ }
    }

    override fun build(): Node {

        toolBar.items.addAll(
                newButton, openButton, saveButton, saveAsButton,
                Separator(),
                undoButton, redoButton, flipXButton, flipYButton,
                Separator(),
                selectToolButton, primitive1dButton, primitive2dButton, deleteToolButton, editGeometryToolButton, editTextToolButton,
                Separator(),
                fillColorPicker.build(), strokeColorPicker.build(),
                lineWidthPicker.build(), strokeJoinPicker.build(), strokeCapPicker.build(),
                Separator(),
                debugButton
        )

        sh.actions.add(ApplicationActions.CLOSE_TAB to { mainWindow.closeTab() })

        return toolBar
    }

}
