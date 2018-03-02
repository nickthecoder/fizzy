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

import uk.co.nickthecoder.fizzy.model.*

interface DrawContext {

    fun clear()

    fun save()

    fun restore()

    fun use(action: () -> Unit): DrawContext {
        save()
        action()
        restore()
        return this
    }

    fun lineColor(color: Paint)
    fun fillColor(color: Paint)

    fun lineDashes(vararg dashes: Double)

    fun translate(by: Dimension2)
    fun translate(x: Dimension, y: Dimension)

    fun rotate(by: Angle)

    fun scale(by: Vector2)


    fun lineWidth(width: Dimension)
    fun lineWidth(width: Double)

    fun beginPath()

    fun endPath(stroke: Boolean, fill: Boolean)

    fun moveTo(point: Dimension2)
    fun moveTo(x: Double, y: Double)

    fun lineTo(point: Dimension2)
    fun lineTo(x: Double, y: Double)

    fun polygon(stroke: Boolean, fill: Boolean, vararg points: Dimension2) {
        beginPath()
        moveTo(points.last())
        points.forEach { lineTo(it) }
        endPath(stroke = stroke, fill = fill)
    }

    fun rectangle(stroke: Boolean, fill: Boolean, start: Dimension2, end: Dimension2) {
        beginPath()
        moveTo(start)
        lineTo(end.x.inDefaultUnits, start.y.inDefaultUnits)
        lineTo(end)
        lineTo(start.x.inDefaultUnits, end.y.inDefaultUnits)
        lineTo(start)
        endPath(stroke, fill)
    }
}
