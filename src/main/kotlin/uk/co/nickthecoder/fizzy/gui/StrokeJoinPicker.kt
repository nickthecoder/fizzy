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
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import uk.co.nickthecoder.fizzy.Fizzy
import uk.co.nickthecoder.fizzy.model.RealShape
import uk.co.nickthecoder.fizzy.model.StrokeJoin
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression


class StrokeJoinPicker(val mainWindow: MainWindow)
    : BuildableNode {

    val button = MenuButton()

    override fun build(): Node {

        StrokeJoin.values().forEach { strokeJoin ->
            val menuItem = MenuItem(strokeJoin.name.toLowerCase())
            menuItem.onAction = EventHandler {
                pick(strokeJoin)
            }
            menuItem.graphic = graphic(strokeJoin)
            button.items.add(menuItem)
        }
        mainWindow.shapeSelectionProperty.addListener { _, _, _ -> onShapeSelectionChanged() }

        button.graphic = graphic(StrokeJoin.BEVEL) // A randomly selected default.

        return button
    }

    fun pick(strokeJoin: StrokeJoin) {

        val history = mainWindow.document?.history ?: return

        history.beginBatch()
        mainWindow.shapeSelectionProperty.value.forEach { shape ->
            if (shape is RealShape) {
                history.makeChange(ChangeExpression(shape.strokeJoin, strokeJoin.toFormula()))
            }
        }
        history.endBatch()
        button.graphic = graphic(strokeJoin)
    }

    fun onShapeSelectionChanged() {
        mainWindow.shapeSelectionProperty.value?.lastOrNull()?.let { shape ->
            if (shape is RealShape) {
                button.graphic = graphic(shape.strokeJoin.value)
            }
        }
    }

    fun graphic(strokeJoin: StrokeJoin): ImageView? {
        return Fizzy.graphic("icons/stroke-join-${strokeJoin.name.toLowerCase()}.png")
    }
}
