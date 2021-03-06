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

import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.view.DrawContext

class CanvasContext(val canvas: Canvas)
    : DrawContext {

    val gc: GraphicsContext = canvas.graphicsContext2D

    init {
        gc.lineCap = StrokeLineCap.BUTT
        gc.lineJoin = StrokeLineJoin.ROUND
    }

    override fun clear() {
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

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

    override fun strokeCap(strokeCap: StrokeCap) {
        when (strokeCap) {
            StrokeCap.BUTT -> gc.lineCap = StrokeLineCap.BUTT
            StrokeCap.ROUND -> gc.lineCap = StrokeLineCap.ROUND
            StrokeCap.SQUARE -> gc.lineCap = StrokeLineCap.SQUARE
        }
    }

    override fun strokeJoin(strokeJoin: StrokeJoin) {
        when (strokeJoin) {
            StrokeJoin.ROUND -> gc.lineJoin = StrokeLineJoin.ROUND
            StrokeJoin.MITER -> gc.lineJoin = StrokeLineJoin.MITER
            StrokeJoin.BEVEL -> gc.lineJoin = StrokeLineJoin.BEVEL
        }
    }

    override fun lineDashes(vararg dashes: Double) {
        gc.setLineDashes(* dashes)
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

    override fun scale(by: Double) {
        gc.scale(by, by)
    }


    override fun lineWidth(width: Dimension) {
        gc.lineWidth = width.inDefaultUnits
    }

    override fun lineWidth(width: Double) {
        gc.lineWidth = width
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

    override fun moveTo(x: Double, y: Double) {
        gc.moveTo(x, y)
    }

    override fun lineTo(point: Dimension2) {
        gc.lineTo(point.x.inDefaultUnits, point.y.inDefaultUnits)
    }

    override fun lineTo(x: Double, y: Double) {
        gc.lineTo(x, y)
    }

    override fun bezierCurveTo(c1: Dimension2, c2: Dimension2, end: Dimension2) {
        gc.bezierCurveTo(
                c1.x.inDefaultUnits, c1.y.inDefaultUnits,
                c2.x.inDefaultUnits, c2.y.inDefaultUnits,
                end.x.inDefaultUnits, end.y.inDefaultUnits
        )
    }

    override fun font(font: FFont) {
        if (font is JavaFXFFont) {
            gc.font = font.fxFont
        }
    }

    override fun multiLineText(multiLineText: MultiLineText, stroke: Boolean, fill: Boolean, clipStart: Dimension2, clipSize: Dimension2) {
        gc.save()
        gc.beginPath()
        gc.rect(clipStart.x.inDefaultUnits, clipStart.y.inDefaultUnits, clipSize.x.inDefaultUnits, clipSize.y.inDefaultUnits)
        gc.clip()
        multiLineText(multiLineText, stroke, fill)
        gc.restore()
    }

    override fun multiLineText(multiLineText: MultiLineText, stroke: Boolean, fill: Boolean) {
        gc.textBaseline = VPos.TOP
        multiLineText.lines.forEach { line ->
            // For visual debugging
            // gc.strokeRect(line.dx.inDefaultUnits, line.dy.inDefaultUnits, line.width.inDefaultUnits, line.height.inDefaultUnits)
            if (fill) {
                gc.fillText(line.text, line.dx.inDefaultUnits, line.dy.inDefaultUnits)
            }
            if (stroke) {
                gc.strokeText(line.text, line.dx.inDefaultUnits, line.dy.inDefaultUnits)
            }
        }
    }

    fun convertPaint(paint: Paint): javafx.scene.paint.Paint {
        if (paint is Color) {
            return javafx.scene.paint.Color(paint.red, paint.green, paint.blue, paint.opacity)
        }
        throw IllegalArgumentException("Unknown paint type : ${paint.javaClass.name}")
    }
}
