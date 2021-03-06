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
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.view.DrawContext

abstract class AbstractGeometryHandle(val shape: Shape, val geometryPart: GeometryPart, val point: Dimension2Expression, val controller: Controller)
    : Handle() {

    override var position: Dimension2
        get() = shape.fromLocalToPage.value * point.value
        set(v) {
            point.formula = (shape.fromPageToLocal.value * v).toFormula()
        }

    override fun isFor(shape: Shape) = shape === this.shape

    override fun dragTo(event: CMouseEvent, dragPoint: Dimension2) {

        val localPoint = shape.fromPageToLocal.value * dragPoint

        shape.document().history.makeChange(
                ChangeExpressions(listOf(point to localPoint.toFormula()))
        )
        controller.dirty.value++
    }

    override fun draw(dc: DrawContext) {
        dc.use {
            dc.rotate(Angle.degrees(45.0))
            super.draw(dc)
        }
    }

    override fun toString() = "GeoHandle @ $position"

}

class GeometryHandle(shape: Shape, geometryPart: GeometryPart, point: Dimension2Expression, controller: Controller)
    : AbstractGeometryHandle(shape, geometryPart, point, controller)
