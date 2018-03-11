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

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.util.toFormula

class FColorPicker(
        val mainWindow: MainWindow,
        val type: String = "fill",
        defaultColor: Color = if (type == "fill") Color.WHITE else Color.BLACK,
        val isStroke: Boolean)

    : BuildableNode {

    val colorPicker = ColorPicker()

    init {
        colorPicker.value = defaultColor
        colorPicker.addEventHandler(ActionEvent.ACTION) { onPickColor() }
    }

    override fun build(): Node {
        colorPicker.styleClass.addAll(type)
        syncObservableList(colorPicker.customColors, mainWindow.customColors)
        mainWindow.shapeSelectionProperty.addListener { _, _, _ -> onShapeSelectionChanged() }

        return colorPicker
    }

    fun onPickColor() {
        val fxColor = colorPicker.value
        val document = mainWindow.document ?: return
        val changes = mutableListOf<Pair<PropExpression<*>, String>>()

        document.history.beginBatch()
        document.selection.forEach { shape ->
            val notTransparent = fxColor.opacity != 0.0
            changes.add((if (isStroke) shape.strokeColor else shape.fillColor) to fxColor.toFizzy().toFormula())
        }
        document.history.makeChange(ChangeExpressions(changes))
        document.history.endBatch()
    }

    fun onShapeSelectionChanged() {
        mainWindow.shapeSelectionProperty.value?.lastOrNull()?.let { shape ->
            val color = (if (isStroke) shape.strokeColor else shape.fillColor).value
            if (color is uk.co.nickthecoder.fizzy.model.Color) {
                colorPicker.value = color.toJavaFX()
            }
        }
    }
}

/**
 * Keeps two lists in sync, so that items added/removed to/from one are also added/removed to/from the other.
 * Note, the order of items is NOT preserved, in fact this treats the list as if it were a Set.
 * This was designed for FColorPicker to sync the preferred colors, and this simplistic implementation is good enough.
 * Also note, it is assumed that the two lists are identical before calling this function.
 */
fun <E> syncObservableList(listA: ObservableList<E>, listB: ObservableList<E>) {
    listA.addListener(object : ListChangeListener<E> {
        override fun onChanged(change: ListChangeListener.Change<out E>?) {
            syncObservableList(listB, change)
        }
    })
    listB.addListener(object : ListChangeListener<E> {
        override fun onChanged(change: ListChangeListener.Change<out E>?) {
            syncObservableList(listA, change)
        }
    })
}

private fun <E> syncObservableList(destList: ObservableList<E>, c: ListChangeListener.Change<out E>?) {
    c ?: return
    while (c.next() == true) {
        println("${c.wasUpdated()}, ${c.wasPermutated()}, ${c.wasReplaced()}")
        if (c.wasAdded()) {
            val toAdd = c.addedSubList.filter { !destList.contains(it) }
            if (toAdd.isNotEmpty()) {
                destList.addAll(toAdd)
            }
        } else if (c.wasRemoved()) {
            val toRemove = c.addedSubList.filter { destList.contains(it) }
            if (toRemove.isNotEmpty()) {
                destList.removeAll(toRemove)
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
