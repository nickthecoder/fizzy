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
package uk.co.nickthecoder.fizzy.model

/**
 * Primitive shapes that are available without needing to load a stencil. Its master shapes are defined programmatically.
 */
object PrimitiveStencil {

    val document = Document()

    val page = Page(document)

    val box = Shape.createBox(page)

    val line = Shape.createLine(page)

    val poly3 = Shape.createPolygon(page, 3)
    val poly4 = Shape.createPolygon(page, 4)
    val poly5 = Shape.createPolygon(page, 5)
    val poly6 = Shape.createPolygon(page, 6)
    val poly7 = Shape.createPolygon(page, 7)
    val poly8 = Shape.createPolygon(page, 8)

    val star3 = Shape.createPolygon(page, 3, star = true)
    val star4 = Shape.createPolygon(page, 4, star = true)
    val star5 = Shape.createPolygon(page, 5, star = true)
    val star6 = Shape.createPolygon(page, 6, star = true)

    val pentangle = Shape.createPolygon(page, 5, star = true)

    init {
        pentangle.controlPoints[0].point.formula = pentangle.geometry.parts[4].point.value.toFormula()
    }
}
