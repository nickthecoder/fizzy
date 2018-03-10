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

class MockContext : AbsoluteContext() {

    val buffer = StringBuffer()
    var debug = false

    override fun clear() {}

    override fun absoluteMoveTo(point: Vector2) {
        buffer.append("M${point.terse()}\n")
    }

    override fun absoluteLineTo(point: Vector2) {
        buffer.append("L${point.terse()}\n")
    }

    override fun absoluteText(point: Vector2, str: String, stroke: Boolean, fill: Boolean) {
        buffer.append("T${point.terse()}:${str.replace("\n", "\\n")}\n")
    }


    override fun fillColor(paint: Paint) {}

    override fun lineColor(paint: Paint) {}

    override fun lineDashes(vararg dashes: Double) {}

    override fun lineWidth(width: Double) {}

    override fun strokeCap(strokeCap: StrokeCap) {}

    override fun strokeJoin(strokeJoin: StrokeJoin) {}

    override fun font(font: FFont) {}

    override fun beginPath() {
        if (debug) {
            println("Begin Path. Transform : ${state.transformation}")
        }
    }

    override fun endPath(stroke: Boolean, fill: Boolean) {
        buffer.append("\n")
        if (debug) {
            println("End Path")
        }
    }

    override fun rotate(by: Angle) {
        if (debug) {
            println("Rotate by ${by.degrees}")
        }
        super.rotate(by)
    }

    override fun scale(by: Vector2) {
        if (debug) {
            println("Scale by $by")
        }
        super.scale(by)
    }

    override fun translate(x: Dimension, y: Dimension) {
        if (debug) {
            println("Translate by $x, $y")
        }
        super.translate(x, y)
    }

    override fun toString() = buffer.toString()
}
