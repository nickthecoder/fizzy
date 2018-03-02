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

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.view.DrawContext

abstract class Handle(val position: Dimension2) {

    abstract fun isFor(shape: Shape): Boolean
    fun draw(dc: DrawContext) {
        dc.use {
            dc.translate(position)
            draw2(dc)
        }
    }

    open fun draw2(dc: DrawContext) {
        dc.beginPath()
        dc.moveTo(-SIZE, -SIZE)
        dc.lineTo(SIZE, -SIZE)
        dc.lineTo(SIZE, SIZE)
        dc.lineTo(-SIZE, SIZE)
        dc.lineTo(-SIZE, -SIZE)
        dc.endPath(true, true)
    }

    companion object {
        val SIZE = 3.0
    }
}

open class ShapeHandle(val shape: Shape, position: Dimension2)
    : Handle(position) {

    override fun isFor(shape: Shape) = shape === this.shape

}

class RotationHandle(shape: Shape, position: Dimension2)
    : ShapeHandle(shape, position) {

    override fun draw2(dc: DrawContext) {
        dc.use {
            dc.rotate(Angle.degrees(45.0))
            super.draw2(dc)
        }
    }
}