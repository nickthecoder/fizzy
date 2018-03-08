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

import uk.co.nickthecoder.fizzy.model.ConnectionPoint
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop

class ConnectTo(prop: Prop<Shape>)
    : TypedMethod1<Shape, ConnectionPoint>(prop, ConnectionPoint::class.java) {

    init {
        listenTo(prop.value.parent.fromPageToLocal)
    }

    /**
     * We must listen to all of the [Prop]s used within the calculation. The base class takes care of listening to
     * the method's receiver and the arguments, but we also use [Shape.fromPageToLocal] of the receiver's parent
     * and [Shape.fromLocalToPage]. The former is taken care of in the constructor, but the latter is checked
     * after ever [eval], because it is possible that the value of otherShape changes (e.g.
     * "this.connectTo(if(myTest, Page.Shape1.Geometry1, Page.Shape2.Geometry2))"
     */
    override fun eval(a: ConnectionPoint): Any {

        val connectionPoint = a
        val otherShape = connectionPoint.shape ?: throw RuntimeException("ConnectionPoint does not have a Shape")
        setShape(otherShape)
        return prop.value.parent.fromPageToLocal.value * otherShape.fromLocalToPage.value * connectionPoint.point.value
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
