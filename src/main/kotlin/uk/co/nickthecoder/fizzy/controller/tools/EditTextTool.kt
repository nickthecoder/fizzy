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
package uk.co.nickthecoder.fizzy.controller.tools

import uk.co.nickthecoder.fizzy.controller.CKeyEvent
import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.ShapeText
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression
import uk.co.nickthecoder.fizzy.model.history.CreateShape
import uk.co.nickthecoder.fizzy.util.toFormula

class EditTextTool(controller: Controller)
    : Tool(controller) {

    var isNewShape = false

    var editingShape: ShapeText? = null

    var caretPosition = 0


    override fun onMousePressed(event: CMouseEvent) {
        controller.page.document.history.endBatch()
        controller.page.document.history.beginBatch()

        val shapes = controller.findShapesAt(event.point)
        shapes.reversed().forEach { shape ->
            if (shape is ShapeText) {
                setShape(shape)
                isNewShape = false
                return
            }
        }
        val localPoint = controller.parent.fromPageToLocal.value * event.point
        val shapeText = ShapeText.create(controller.parent)
        shapeText.transform.pin.formula = localPoint.toFormula()
        shapeText.fontSize.formula = "46pt"
        shapeText.fillColor.formula = "BLACK"
        shapeText.strokeColor.formula = "TRANSPARENT"
        setShape(shapeText)
        isNewShape = true
    }

    private fun setShape(shape: ShapeText) {
        editingShape = shape
        caretPosition = 0
    }

    override fun onKeyTyped(event: CKeyEvent) {
        editingShape?.let {
            if (isNewShape) {
                isNewShape = false
                controller.page.document.history.makeChange(CreateShape(it, controller.parent))
            }

            val text = it.text.value
            val newText = text.substring(0, caretPosition) + event.text + text.substring(caretPosition)
            caretPosition++
            controller.page.document.history.makeChange(ChangeExpression(it.text, newText.toFormula()))
            event.consume()
        }
    }

    override fun onKeyPressed(event: CKeyEvent) {
        editingShape?.let { shape ->
            val oldText = shape.text.value
            var newText: String? = null

            var used = true
            when (event.key) {
                CKeyEvent.ENTER -> {
                    newText = (oldText.substring(0, caretPosition) + "\n" + oldText.substring(caretPosition))
                    caretPosition++
                }
                CKeyEvent.BACK_SPACE -> {
                    if (caretPosition > 0) {
                        newText = oldText.substring(0, caretPosition - 1) + oldText.substring(caretPosition)
                        caretPosition--
                    }
                }
                CKeyEvent.DELETE -> {
                    if (caretPosition < oldText.length) {
                        newText = oldText.substring(0, caretPosition) + oldText.substring(caretPosition + 1)
                    }
                }
                CKeyEvent.LEFT -> {
                    if (caretPosition > 0) {
                        caretPosition--
                    }
                }
                CKeyEvent.RIGHT -> {
                    if (caretPosition < oldText.length) {
                        caretPosition++
                    }
                }
                CKeyEvent.HOME -> {
                    caretPosition = 0
                }
                CKeyEvent.END -> {
                    caretPosition = oldText.length
                }
                CKeyEvent.ESCAPE -> {
                    controller.tool = SelectTool(controller)
                    return
                }
                else -> {
                    used = false
                }
            }

            if (newText != null) {
                controller.page.document.history.makeChange(ChangeExpression(shape.text, newText.toFormula()))
            }

            if (used) {
                event.consume()
            }
        }
    }

    override fun endTool(replacement: Tool) {
        controller.page.document.history.endBatch()
    }

}
