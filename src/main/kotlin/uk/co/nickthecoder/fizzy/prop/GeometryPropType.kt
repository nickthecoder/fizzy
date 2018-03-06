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

import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo


class GeometryPropType private constructor()
    : PropType<Geometry>(Geometry::class) {

    override fun findField(prop: Prop<Geometry>, name: String): Prop<*>? {

        return when (name) {
            "Fill" -> prop.value.fill
            "Stroke" -> prop.value.stroke
            "Connect" -> prop.value.connect
            else -> {
                println("Looking for $name for GeometryPropType")
                // Allow access to any of the Geometries parts, without the hassle of ".parts.xxx"
                val partsListProp = PropValue(prop.value.parts)
                val foundField = partsListProp.field(name)
                if (foundField == null) {
                    println("Nope")
                    super.findField(prop, name)
                } else {
                    println("Yep")
                    // Note. "prop" even will be of type ListPropertyAccess, as that is the only way to get here.
                    @Suppress("UNCHECKED_CAST")
                    GeometryPartsFieldProp(prop, foundField as Prop<FList<GeometryPart>>)
                }
            }
        }
    }

    companion object {
        val instance = GeometryPropType()
    }
}

class GeometryPartsFieldProp(val propGeometry: Prop<Geometry>, val wrappedField: Prop<FList<GeometryPart>>)
    : PropCalculation<Any>() {

    init {
        if (propGeometry is PropListener) {
            propGeometry.propListeners.add(this)
        }
    }

    override fun eval(): Any {
        return wrappedField.value
    }

    override val propListenerOwner: String
        get() = "GeometryPartsFieldProp"
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
            "Point" -> prop.value.point
            else -> null
        }
    }

    companion object {
        val instance = LineToPropType()
    }
}
