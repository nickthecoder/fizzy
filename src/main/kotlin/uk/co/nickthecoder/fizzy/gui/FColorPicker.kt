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

import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color
import uk.co.nickthecoder.fizzy.model.Paint
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.PaintExpression
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.util.toFormula

class FColorPicker(
        val mainWindow: MainWindow,
        val type: String = "fill",
        val findPaintExp: (Shape) -> PaintExpression?)
    : BuildableNode {

    val colorPicker = ColorPicker()

    init {
        colorPicker.addEventHandler(ActionEvent.ACTION) { onPickColor() }
    }

    override fun build(): Node {
        colorPicker.styleClass.addAll("button", type)
        colorPicker

        mainWindow.shapeSelectionProperty.addListener { _, _, _ -> onShapeSelectionChanged() }

        return colorPicker
    }

    fun onPickColor() {
        val fxColor = colorPicker.value
        val document = mainWindow.document ?: return
        val changes = mutableListOf<Pair<PropExpression<Paint>, String>>()

        document.history.beginBatch()
        document.selection.forEach { shape ->
            findPaintExp(shape)?.let {
                changes.add(it to fxColor.toFormula())
            }
        }
        document.history.makeChange(ChangeExpressions(changes))
        document.history.endBatch()
    }

    fun onShapeSelectionChanged() {
        mainWindow.shapeSelectionProperty.value?.lastOrNull()?.let { shape ->
            findPaintExp(shape)?.let { paint ->
                val color = paint.value
                if (color is uk.co.nickthecoder.fizzy.model.Color) {
                    colorPicker.value = color.toJavaFX()
                }
            }
        }
    }
}

fun Color.toFizzy(): uk.co.nickthecoder.fizzy.model.Color {
    return uk.co.nickthecoder.fizzy.model.Color(this.red, this.green, this.blue, this.opacity)
}

fun uk.co.nickthecoder.fizzy.model.Color.toJavaFX(): Color {
    return Color(this.red, this.green, this.blue, this.opacity)
}

fun Color.toFormula(): String = if (this.isOpaque())
    "RGB(${this.red.toFormula()},${this.green.toFormula()},${this.blue.toFormula()})"
else
    "RGBA(${this.red.toFormula()},${this.green.toFormula()},${this.blue.toFormula()},${this.opacity.toFormula()})"
