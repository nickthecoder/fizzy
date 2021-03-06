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
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.collection.ListListener
import uk.co.nickthecoder.fizzy.controller.tools.GrowShape1dTool
import uk.co.nickthecoder.fizzy.controller.tools.SelectTool
import uk.co.nickthecoder.fizzy.controller.tools.StampShape2dTool
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.model.history.CreateShape
import uk.co.nickthecoder.fizzy.model.history.DeleteShape
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.view.ShapeView

class StencilView(val mainWindow: MainWindow, val stencil: Document, val localMasters: Boolean = false)
    : BuildableNode {

    val titledPane = TitledPane()

    val buttons = FlowPane()

    val pageChangeListener = object : ListListener<Shape> {
        override fun added(list: FList<Shape>, item: Shape, index: Int) {
            addShape(item)
        }

        override fun removed(list: FList<Shape>, item: Shape, index: Int) {
            removeShape(item)
        }
    }

    override fun build(): TitledPane {
        titledPane.isExpanded = false
        titledPane.content = buttons

        if (localMasters) {
            titledPane.text = "Local"
            addPage(stencil.localMasterShapes)
        } else {
            titledPane.text = stencil.name.value
            stencil.pages.forEach { page ->
                addPage(page)
            }
        }

        titledPane.addEventHandler(MouseEvent.MOUSE_PRESSED) { checkMenu(it) }
        titledPane.addEventHandler(MouseEvent.MOUSE_RELEASED) { checkMenu(it) }

        return titledPane
    }

    fun addPage(page: Page) {
        page.children.listeners.add(pageChangeListener)
        page.children.forEach { shape ->
            addShape(shape)
        }
    }

    fun addShape(shape: Shape) {
        val button = StencilButton(mainWindow, shape)
        buttons.children.add(button)
    }

    fun removeShape(shape: Shape) {
        buttons.children.removeIf { it is StencilButton && it.shape === shape }
    }

    fun checkMenu(event: MouseEvent) {
        if (event.isPopupTrigger) {
            event.consume()
            val menu = ContextMenu()

            val newShape1dMI = MenuItem("New 1D Shape")
            newShape1dMI.onAction = EventHandler { createNewShape1d() }
            val newShape2dMI = MenuItem("New 2D Shape")
            newShape2dMI.onAction = EventHandler { createNewShape2d() }
            menu.items.addAll(newShape1dMI, newShape2dMI)

            menu.show(titledPane, event.screenX, event.screenY)
        }
    }

    fun createNewShape1d() {
        val shape = Shape.createLine(stencil.pages[0], end = "Dimension2(100mm, 0mm)")
        stencil.history.makeChange(CreateShape(shape, stencil.pages[0]))
    }

    fun createNewShape2d() {
        val shape = Shape.createBox(stencil.pages[0])
        stencil.history.makeChange(CreateShape(shape, stencil.pages[0]))
    }
}

class StencilButton(val mainWindow: MainWindow, val shape: Shape)
    : VBox() {

    val button = ToggleButton()
    val nameLabel = Label(shape.name.value)

    init {
        styleClass.add("stencil")
        button.graphic = ShapeGraphic(shape, BUTTON_SIZE)

        button.onAction = EventHandler {
            mainWindow.controller?.let { controller ->
                if (button.isSelected) {
                    if (shape is Shape2d) {
                        controller.tool = StampShape2dTool(controller, shape) { button.isSelected = false }
                    } else if (shape is Shape1d) {
                        controller.tool = GrowShape1dTool(controller, shape)
                    }
                    // TODO Handle ShapeText too
                } else {
                    controller.tool = SelectTool(controller)
                }
            }
            button.parent.requestFocus()
        }

        button.addEventFilter(MouseEvent.MOUSE_PRESSED) { checkMenu(it) }
        button.addEventFilter(MouseEvent.MOUSE_RELEASED) { checkMenu(it) }

        nameLabel.maxWidth = MAX_WIDTH
        this.minWidth = MAX_WIDTH + 6.0
        this.maxWidth = MAX_WIDTH + 6.0

        children.addAll(button, nameLabel)
    }

    fun checkMenu(event: MouseEvent) {
        if (event.isPopupTrigger) {
            event.consume()
            val menu = ContextMenu()

            val editMaster = MenuItem("Edit")
            editMaster.onAction = EventHandler { mainWindow.editMaster(shape) }

            val editShapeSheet = MenuItem("Edit Shape Sheet")
            editShapeSheet.onAction = EventHandler { mainWindow.editShapeSheet(shape) }

            val duplicateShape = MenuItem("Duplicate Shape")
            duplicateShape.onAction = EventHandler { shape.duplicate() }

            val deleteMaster = MenuItem("Delete")
            deleteMaster.onAction = EventHandler {
                val history = shape.document().history
                history.makeChange(DeleteShape(shape))
            }

            menu.items.addAll(editMaster, editShapeSheet, duplicateShape, deleteMaster)
            menu.show(button.parent, event.screenX, event.screenY)
        }
    }

    companion object {
        val MAX_WIDTH = 80.0
        val BUTTON_SIZE = 50.0
        val MARGIN = 3.0
    }
}

class ShapeGraphic(val shape: Shape, val size: Double, val margin: Double = StencilButton.MARGIN)
    : Canvas(size, size), ChangeListener<Shape> {

    init {
        redraw()
        shape.changeListeners.add(this)
    }

    override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
        redraw()
    }

    fun redraw() {
        val dc = CanvasContext(this)
        dc.clear()

        dc.use {
            val width = shape.size.value.x.inDefaultUnits
            val height = shape.size.value.y.inDefaultUnits
            //dc.translate(Dimension(-margin), Dimension(-margin))
            dc.translate(Dimension(size / 2.0), Dimension(size / 2.0))
            if (width > height) {
                dc.scale((size - margin * 2) / width)
            } else {
                dc.scale((size - margin * 2) / height)
            }
            dc.translate(-shape.transform.pin.value)
            ShapeView(shape, dc).draw()
        }
    }
}
