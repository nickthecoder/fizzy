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
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions

class GeometryHandle(val shape: Shape, val geometryPart: GeometryPart, val controller: Controller)
    : Handle() {

    override var position: Dimension2
        get() = shape.fromLocalToPage.value * geometryPart.point.value
        set(v) {
            geometryPart.point.formula = (shape.fromPageToLocal.value * v).toFormula()
        }

    override fun isFor(shape: Shape) = shape === this.shape

    override fun dragTo(event: CMouseEvent, dragPoint: Dimension2) {

        val localPoint = shape.fromPageToLocal.value * dragPoint

        shape.document().history.makeChange(
                ChangeExpressions(listOf(geometryPart.point to localPoint.toFormula()))
        )
        controller.dirty.value++
    }

    override fun toString() = "GeoHandle @ $position"

}
