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
package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart

class AddGeometryPart(val shape: Shape, val index: Int, val geometryPart: GeometryPart)
    : Change {

    override fun redo() {
        shape.geometry.parts.add(index, geometryPart)
    }

    override fun undo() {
        shape.geometry.parts.removeAt(index)
    }
}