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
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.image.ImageView
import uk.co.nickthecoder.fizzy.Fizzy
import uk.co.nickthecoder.fizzy.controller.tools.GrowShape1dTool
import uk.co.nickthecoder.fizzy.controller.tools.GrowShape2dTool
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.Shape2d

class ShapePickerItem(val name: String, val label: String, val masterShape: Shape) {

    val icon = Fizzy.imageResource("icons/$name.png")

    val menuItem = MenuItem(label, icon?.let { ImageView(it) })

}

class ShapePicker(val mainWindow: MainWindow, val items: Array<ShapePickerItem>, val defaultIndex: Int = 0)
    : BuildableNode {

    val button = SplitMenuButton()
    var currentItem: ShapePickerItem? = null
        set(v) {
            field = v
            button.graphic = v?.icon?.let { ImageView(it) }
        }

    override fun build(): SplitMenuButton {

        items.forEach { item ->
            val menuItem = item.menuItem
            menuItem.onAction = EventHandler {
                currentItem = item
                pickShape(item.masterShape)
            }
            button.items.add(menuItem)
        }

        currentItem = items[defaultIndex]
        button.onAction = EventHandler { onAction() }

        return button
    }

    fun pickShape(shape: Shape) {
        val strokeColor = mainWindow.toolBar.strokeColorPicker.colorPicker.value.toFizzy()
        val fillColor = mainWindow.toolBar.fillColorPicker.colorPicker.value.toFizzy()

        mainWindow.documentTab?.drawingArea?.controller?.let { controller ->
            when (shape) {
                is Shape1d -> controller.tool = GrowShape1dTool(controller, shape, strokeColor, fillColor)
                is Shape2d -> controller.tool = GrowShape2dTool(controller, shape, strokeColor, fillColor)
                else -> {
                    //controller.tool = StampShape2dTool(controller, shape)
                }
            }
        }
    }

    fun onAction() {
        currentItem?.let { pickShape(it.masterShape) }
    }
}
