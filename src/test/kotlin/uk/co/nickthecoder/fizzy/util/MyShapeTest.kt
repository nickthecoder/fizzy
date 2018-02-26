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
package uk.co.nickthecoder.fizzy.util

import uk.co.nickthecoder.fizzy.model.*

interface MyShapeTest {

    fun createBox(parent: Parent, size: String, at: String): Shape2d {
        val box = Shape2d.create(parent)
        box.size.expression = size
        box.transform.pin.expression = at
        val geometry = Geometry()
        box.geometries.add(geometry)
        // Make a box
        geometry.parts.add(MoveTo("Size * Vector2(0,0)"))
        geometry.parts.add(LineTo("Size * Vector2(1,0)"))
        geometry.parts.add(LineTo("Size * Vector2(1,1)"))
        geometry.parts.add(LineTo("Size * Vector2(0,1)"))
        geometry.parts.add(LineTo("Geometry1.Point1"))

        return box
    }

    fun createLine(parent: Parent, start: String, end: String, lineWidth: String = "2mm"): Shape1d {
        val line = Shape1d.create(parent)

        line.start.expression = start
        line.end.expression = end
        line.lineWidth.expression = lineWidth

        val geometry = Geometry()
        line.geometries.add(geometry)

        geometry.parts.add(MoveTo("Dimension2(0mm,LineWidth/2)"))
        geometry.parts.add(LineTo("Dimension2(Length,LineWidth/2)"))

        return line
    }
}
