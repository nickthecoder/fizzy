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
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.fizzy.controller.tools.GrowShape1dTool
import uk.co.nickthecoder.fizzy.controller.tools.SelectTool
import uk.co.nickthecoder.fizzy.controller.tools.StampShape2dTool
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.view.ShapeView

class StencilView(val mainWindow: MainWindow, val stencil: Document)
    : BuildableNode {

    val titledPane = TitledPane()

    val buttons = FlowPane()

    override fun build(): TitledPane {
        titledPane.isExpanded = true
        titledPane.text = stencil.name
        titledPane.content = buttons

        stencil.pages.forEach { page ->
            page.children.forEach { shape ->
                val button = StencilButton(mainWindow, shape)
                buttons.children.add(button.build())
            }
        }

        return titledPane
    }
}

class StencilButton(val mainWindow: MainWindow, val shape: Shape)
    : BuildableNode {

    val button = ToggleButton()
    val nameLabel = Label(shape.name.value)

    val vBox = VBox()

    override fun build(): Node {

        println("Shape name = ${shape.name.value}")

        vBox.styleClass.add("stencil")
        button.graphic = ShapeGraphic(shape, SIZE)

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

        vBox.children.addAll(button, nameLabel)
        return vBox
    }

    companion object {
        val SIZE = 50.0
        val MARGIN = 3.0
    }
}

class ShapeGraphic(val shape: Shape, size: Double, margin: Double = StencilButton.MARGIN)
    : Canvas(size, size) {

    init {
        val dc = CanvasContext(this)

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
