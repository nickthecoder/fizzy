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
import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.fizzy.controller.tools.GrowShape1dTool
import uk.co.nickthecoder.fizzy.controller.tools.StampShape2dTool
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.Shape2d

class StencilView(val mainWindow: MainWindow, val stencil: Document)
    : BuildableNode {

    val titledPane = TitledPane()

    val buttons = FlowPane()

    override fun build(): TitledPane {
        titledPane.text = stencil.name
        titledPane.content = buttons

        println("Creating Stencil view has ${stencil.pages.size} pages")
        stencil.pages.forEach { page ->
            println("Stencil page has ${page.children.size} shapes")
            page.children.forEach { shape ->
                println("Stencil Shape")
                val button = StencilButton(mainWindow, shape)
                buttons.children.add(button.build())
            }
        }

        return titledPane
    }
}

class StencilButton(val mainWindow: MainWindow, val shape: Shape)
    : BuildableNode {

    val button = Button("?")
    val nameLabel = Label(shape.name.value)

    val vBox = VBox()

    override fun build(): Node {

        vBox.styleClass.add("stencil")

        button.onAction = EventHandler {
            if (shape is Shape2d) {
                mainWindow.controller?.let { it.tool = StampShape2dTool(it, shape) }
            } else if (shape is Shape1d) {
                mainWindow.controller?.let {
                    it.tool = GrowShape1dTool(it, shape)
                }
            }
            // TODO Handle ShapeText too
        }

        vBox.children.addAll(button, nameLabel)
        return vBox
    }
}
