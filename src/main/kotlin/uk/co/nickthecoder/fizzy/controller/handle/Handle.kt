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
package uk.co.nickthecoder.fizzy.controller.handle

import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.view.DrawContext

abstract class Handle(var position: Dimension2) {

    abstract fun isFor(shape: Shape): Boolean

    open fun draw(dc: DrawContext) {
        dc.beginPath()
        dc.moveTo(-SIZE, -SIZE)
        dc.lineTo(SIZE, -SIZE)
        dc.lineTo(SIZE, SIZE)
        dc.lineTo(-SIZE, SIZE)
        dc.lineTo(-SIZE, -SIZE)
        dc.endPath(true, true)
    }

    fun isAt(point: Dimension2, scale: Double): Boolean {
        val delta = (point - position) / scale
        return Math.abs(delta.x.inDefaultUnits) < NEAR / scale && Math.abs(delta.y.inDefaultUnits) < NEAR / scale
    }

    open fun beginDrag(startPoint: Dimension2) {}

    abstract fun dragTo(event: CMouseEvent, dragPoint: Dimension2)

    companion object {
        val SIZE = 3.0
        val NEAR = SIZE + 1.0
    }
}
