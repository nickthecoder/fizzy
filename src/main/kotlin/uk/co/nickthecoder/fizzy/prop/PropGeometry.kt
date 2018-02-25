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
package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Geometry
import uk.co.nickthecoder.fizzy.model.LineTo
import uk.co.nickthecoder.fizzy.model.MoveTo


class GeometryPropType private constructor()
    : PropType<Geometry>(Geometry::class) {

    override fun findField(prop: Prop<Geometry>, name: String): Prop<*>? {

        // Allow access to any of the Geometries parts, without the hassle of ".parts.xxx"
        val partsProp = FListProp(prop.value.parts)
        val found = partsProp.field(name)
        return found ?: super.findField(prop, name)
    }

    companion object {
        val instance = GeometryPropType()
    }
}

class MoveToPropType private constructor()
    : PropType<MoveTo>(MoveTo::class) {

    override fun findField(prop: Prop<MoveTo>, name: String): Prop<*>? {
        return when (name) {
            "Point" -> prop.value.point
            else -> null
        }
    }

    companion object {
        val instance = MoveToPropType()
    }
}

class LineToPropType private constructor()
    : PropType<LineTo>(LineTo::class) {

    override fun findField(prop: Prop<LineTo>, name: String): Prop<*>? {
        return when (name) {
            "point" -> prop.value.point
            else -> null
        }
    }

    companion object {
        val instance = LineToPropType()
    }
}
