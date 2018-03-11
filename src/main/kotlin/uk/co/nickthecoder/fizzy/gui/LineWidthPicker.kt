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
import uk.co.nickthecoder.fizzy.Fizzy
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.ShapeText
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.util.toFormula


class LineWidthPicker(val mainWindow: MainWindow, val widths: Array<Dimension>)
    : BuildableNode {

    val button = MenuButton()

    override fun build(): Node {

        button.graphic = Fizzy.graphic("icons/format-stroke-width.png")
        val noStrokeMenuItem = MenuItem("None")
        noStrokeMenuItem.onAction = EventHandler {
            noStroke()
        }
        button.items.add(noStrokeMenuItem)

        widths.forEach { width ->
            val menuItem = MenuItem(width.toFormula())
            menuItem.onAction = EventHandler {
                pickWidth(width)
            }
            button.items.add(menuItem)
        }

        return button
    }

    fun noStroke() {
        val history = mainWindow.document?.history ?: return

        history.beginBatch()
        mainWindow.shapeSelectionProperty.value.forEach { shape ->
            history.makeChange(ChangeExpressions(listOf(
                    shape.geometry.stroke to false.toFormula()
            )))
            if (shape is ShapeText) {
                history.makeChange(ChangeExpressions(listOf(
                        shape.stroke to false.toFormula()
                )))
            }
        }
        history.endBatch()

    }

    fun pickWidth(width: Dimension) {

        val history = mainWindow.document?.history ?: return

        history.beginBatch()
        mainWindow.shapeSelectionProperty.value.forEach { shape ->
            history.makeChange(ChangeExpressions(listOf(
                    shape.lineWidth to width.toFormula()
            )))
            history.makeChange(ChangeExpressions(listOf(
                    shape.geometry.stroke to true.toFormula()
            )))

            if (shape is ShapeText) {
                history.makeChange(ChangeExpressions(listOf(
                        shape.stroke to true.toFormula()
                )))
            }
        }
        history.endBatch()
    }

}
