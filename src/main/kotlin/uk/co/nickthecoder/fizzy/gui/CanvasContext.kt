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

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.view.DrawContext

class CanvasContext(canvas: Canvas)
    : DrawContext {

    val gc: GraphicsContext = canvas.graphicsContext2D

    override fun save() {
        gc.save()
    }

    override fun restore() {
        gc.restore()
    }

    override fun fillColor(paint: Paint) {
        gc.fill = convertPaint(paint)
    }

    override fun lineColor(paint: Paint) {
        gc.stroke = convertPaint(paint)
    }

    override fun translate(by: Dimension2) {
        gc.translate(by.x.inDefaultUnits, by.y.inDefaultUnits)
    }

    override fun translate(x: Dimension, y: Dimension) {
        gc.translate(x.inDefaultUnits, y.inDefaultUnits)
    }

    override fun rotate(by: Angle) {
        gc.rotate(by.degrees)
    }

    override fun scale(by: Vector2) {
        gc.scale(by.x, by.y)
    }


    override fun lineWidth(width: Dimension) {
        gc.lineWidth = width.inDefaultUnits
    }


    override fun beginPath() {
        gc.beginPath()
    }

    override fun endPath(stroke: Boolean, fill: Boolean) {
        if (fill) {
            gc.fill()
        }
        if (stroke) {
            gc.stroke()
        }
    }

    override fun moveTo(point: Dimension2) {
        gc.moveTo(point.x.inDefaultUnits, point.y.inDefaultUnits)
    }

    override fun lineTo(point: Dimension2) {
        gc.lineTo(point.x.inDefaultUnits, point.y.inDefaultUnits)
    }

    fun convertPaint(paint: Paint): javafx.scene.paint.Paint {
        if (paint is Color) {
            return javafx.scene.paint.Color(paint.red, paint.green, paint.blue, paint.opacity)
        }
        throw IllegalArgumentException("Unknown paint type : ${paint.javaClass.name}")
    }
}
