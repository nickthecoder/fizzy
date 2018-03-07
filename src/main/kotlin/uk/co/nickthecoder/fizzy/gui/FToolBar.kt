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

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ToolBar
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.controller.tools.*
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.PrimitiveStencil
import uk.co.nickthecoder.fizzy.model.RealShape

class FToolBar(val mainWindow: MainWindow)
    : BuildableNode {

    val toolBar = ToolBar()

    val document: Document?
        get() = mainWindow.document

    val drawingArea: DrawingArea?
        get() = mainWindow.documentTab?.drawingArea

    val controller: Controller?
        get() = drawingArea?.controller

    val undoButton = Button("Undo")
    val redoButton = Button("Redo")
    val selectToolButton = Button("Select")
    val deleteToolButton = Button("Delete")
    val stampToolButton = Button("Stamp")
    val boxToolButton = Button("Box")
    val lineToolButton = Button("Line")
    val pentagonToolButton = Button("Pentagon")
    val starToolButton = Button("Star")
    val debugButton = Button("Debug")

    val lineColorPicker = FColorPicker(mainWindow, "stroke") { shape -> if (shape is RealShape) shape.strokeColor else null }
    val fillColorPicker = FColorPicker(mainWindow, "fill") { shape -> if (shape is RealShape) shape.fillColor else null }

    override fun build(): Node {

        undoButton.onAction = EventHandler { document?.history?.undo() }
        redoButton.onAction = EventHandler { document?.history?.redo() }
        selectToolButton.onAction = EventHandler { controller?.let { it.tool = SelectTool(it) } }
        deleteToolButton.onAction = EventHandler { controller?.let { it.tool = DeleteTool(it) } }
        stampToolButton.onAction = EventHandler { controller?.let { it.tool = StampShape2dTool(it, PrimitiveStencil.pentangle) } }
        boxToolButton.onAction = EventHandler { controller?.let { it.tool = GrowShape2dTool(it, PrimitiveStencil.box) } }
        lineToolButton.onAction = EventHandler { controller?.let { it.tool = GrowShape1dTool(it, PrimitiveStencil.line) } }
        pentagonToolButton.onAction = EventHandler { controller?.let { it.tool = GrowShape2dTool(it, PrimitiveStencil.pentagon) } }
        starToolButton.onAction = EventHandler { controller?.let { it.tool = GrowShape2dTool(it, PrimitiveStencil.star) } }
        debugButton.onAction = EventHandler { mainWindow.debug() }

        toolBar.items.addAll(
                undoButton, redoButton,
                selectToolButton, deleteToolButton,
                stampToolButton,
                boxToolButton, lineToolButton, pentagonToolButton, starToolButton,
                debugButton,
                lineColorPicker.build(), fillColorPicker.build()

        )

        return toolBar
    }

}
