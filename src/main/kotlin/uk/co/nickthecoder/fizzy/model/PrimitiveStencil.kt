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

    val box = Shape.createBox(page, "Dimension2(200mm,100mm)")

    val line = Shape.createLine(page, "Dimension2(0mm,0mm)", "Dimension2(10mm,0mm)")

    val pentagon = Shape.createPolygon(page, 5, Dimension(60.0, Dimension.Units.mm))

    val star = Shape.createPolygon(page, 5, Dimension(60.0, Dimension.Units.mm), star = true)

    val pentangle = Shape.createPolygon(page, 5, Dimension(60.0, Dimension.Units.mm), star = true)

    init {
        pentangle.controlPoints[0].value.point.formula = pentangle.geometries[0].value.parts[4].point.value.toFormula()
    }
}
