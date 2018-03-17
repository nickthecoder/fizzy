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

import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.view.DrawContext

class BezierGeometryHandle(
        shape: Shape,
        point: Dimension2Expression,
        val otherPoint: Dimension2Expression,
        controller: Controller)

    : GeometryHandle(shape, point, controller) {

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.beginPath()
            dc.moveTo(Dimension2.ZERO_mm)
            val m = shape.fromLocalToPage.value
            dc.lineTo(m * otherPoint.value - m * point.value)
            dc.endPath(true, false)
            super.draw(dc)
        }
    }
}
