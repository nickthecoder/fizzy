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
package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.prop.Prop

class ConnectAlong(prop: Prop<Shape>)
    : TypedMethod2<Shape, Geometry, Double>(prop, Geometry::class.java, Double::class.java) {

    init {
        listenTo(prop.value.parent.fromPageToLocal)
    }

    override fun eval(a: Geometry, b: Double): Any {
        val geometry = a
        val along = b

        setShape(geometry.shape)
        val point = geometry.pointAlong(along)
        return prop.value.parent.fromPageToLocal.value * geometry.shape.fromLocalToPage.value * point
    }

    var prevShape: Shape? = null

    fun setShape(shape: Shape) {
        if (shape != prevShape) {
            prevShape?.let {
                unlistenTo(it.fromLocalToPage)
            }
            listenTo(shape.fromLocalToPage)
        }
    }

}
