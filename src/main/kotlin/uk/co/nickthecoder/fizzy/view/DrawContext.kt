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

    fun save()

    fun restore()

    fun use(action: () -> Unit): DrawContext {
        save()
        action()
        restore()
        return this
    }

    fun lineColor(color : Paint)
    fun fillColor(color : Paint)

    fun translate(by: Dimension2)
    fun translate(x: Dimension, y: Dimension)

    fun rotate(by: Angle)

    fun scale(by: Vector2)


    fun lineWidth(width: Dimension)


    fun beginPath()

    fun endPath(stroke: Boolean, fill: Boolean)

    fun moveTo(point: Dimension2)

    fun lineTo(point: Dimension2)

}
