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
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression

class Shape1dHandle(val shape1d: Shape1d, position: Dimension2, val controller: Controller, val isEnd: Boolean)
    : ShapeHandle(shape1d, position) {

    override fun dragTo(event: CMouseEvent, dragPoint: Dimension2) {
        val startEnd = if (isEnd) shape1d.end else shape1d.start

        val (connectFormula, _, geometry) = Controller.connectData(dragPoint, shape1d, event.scale)
        controller.highlightGeometry.value = geometry ?: Controller.NO_GEOMETRY

        shape.document().history.makeChange(
                ChangeExpression(startEnd, connectFormula ?: dragPoint.toFormula())
        )
    }
}
