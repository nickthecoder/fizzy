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
package uk.co.nickthecoder.fizzy.view

import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.ShapeText
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.model.geometry.BezierCurveTo
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo

abstract class View(val dc: DrawContext) {

    abstract fun draw()

    fun drawShape(shape: Shape) {
        dc.use {

            dc.translate(shape.transform.pin.value)
            dc.scale(shape.transform.scale.value)
            dc.rotate(shape.transform.rotation.value)
            if (shape.transform.flipX.value) {
                dc.scale(Vector2(-1.0, 1.0))
            }
            if (shape.transform.flipY.value) {
                dc.scale(Vector2(1.0, -1.0))
            }
            dc.translate(-shape.transform.locPin.value) // Inefficient

            dc.lineWidth(shape.lineWidth.value)
            dc.lineColor(shape.strokeColor.value)
            dc.fillColor(shape.fillColor.value)
            dc.strokeJoin(shape.strokeJoin.value)
            dc.strokeCap(shape.strokeCap.value)

            dc.beginPath()
            shape.geometry.parts.forEach { part ->
                when (part) {
                    is MoveTo -> dc.moveTo(part.point.value)

                    is BezierCurveTo -> {
                        dc.bezierCurveTo(part.a.value, part.b.value, part.point.value)
                    }

                    is LineTo -> dc.lineTo(part.point.value)
                }
            }
            dc.endPath(shape.strokeColor.value.isVisible(), shape.fillColor.value.isVisible())

            if (shape is ShapeText) {
                dc.translate(shape.transform.locPin.value)
                dc.font(shape.font.value)
                if (shape.clip.value) {
                    dc.multiLineText(shape.multiLineText.value, stroke = shape.strokeColor.value.isVisible(), fill = shape.fillColor.value.isVisible(),
                            clipStart = -shape.transform.locPin.value, clipSize = shape.size.value)
                } else {
                    dc.multiLineText(shape.multiLineText.value, stroke = shape.strokeColor.value.isVisible(), fill = shape.fillColor.value.isVisible())
                }
            }

            shape.children.forEach { child ->
                drawShape(child)
            }
        }
    }

}