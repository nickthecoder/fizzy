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
import uk.co.nickthecoder.fizzy.model.StrokeCap
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression


class StrokeCapPicker(val mainWindow: MainWindow)
    : BuildableNode {

    val button = MenuButton()

    override fun build(): Node {

        StrokeCap.values().forEach { strokeCap ->
            val menuItem = MenuItem(strokeCap.name.toLowerCase())
            menuItem.onAction = EventHandler {
                pick(strokeCap)
            }
            menuItem.graphic = graphic(strokeCap)
            button.items.add(menuItem)
        }
        mainWindow.shapeSelectionProperty.addListener { _, _, _ -> onShapeSelectionChanged() }

        button.graphic = graphic(StrokeCap.ROUND) // A randomly selected default.

        return button
    }

    fun pick(strokeCap: StrokeCap) {

        val history = mainWindow.document?.history ?: return

        history.beginBatch()
        mainWindow.shapeSelectionProperty.value.forEach { shape ->
            if (shape is RealShape) {
                history.makeChange(ChangeExpression(shape.strokeCap, strokeCap.toFormula()))
            }
        }
        history.endBatch()
        button.graphic = graphic(strokeCap)
    }

    fun onShapeSelectionChanged() {
        mainWindow.shapeSelectionProperty.value?.lastOrNull()?.let { shape ->
            if (shape is RealShape) {
                button.graphic = graphic(shape.strokeCap.value)
            }
        }
    }

    fun graphic(strokeCap: StrokeCap): ImageView? {
        return Fizzy.graphic("icons/stroke-cap-${strokeCap.name.toLowerCase()}.png")
    }
}
