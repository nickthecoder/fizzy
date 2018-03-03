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

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression
import uk.co.nickthecoder.fizzy.view.DrawContext

class RotationHandle(shape: Shape, position: Dimension2)
    : ShapeHandle(shape, position) {

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.rotate(Angle.degrees(45.0))
            super.draw(dc)
        }
    }

    override fun dragTo(pagePosition: Dimension2, constrain : Boolean) {
        val local = shape.fromPageToLocal.value * pagePosition
        val angle = (local - shape.transform.locPin.value).angle() + Angle.degrees(90.0)
        shape.document().history.makeChange(ChangeExpression(
                shape.transform.rotation,
                (shape.transform.rotation.value + angle).toFormula()))
    }
}
