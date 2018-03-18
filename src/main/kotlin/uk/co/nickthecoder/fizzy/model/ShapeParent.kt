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

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.prop.Prop

interface ShapeParent {

    val children: MutableFList<Shape>

    fun page(): Page

    fun document(): Document

    fun findShape(name: String): Shape?

    fun findNearestConnectionPoint(atPagePoint: Dimension2, exclude: Shape): Pair<ConnectionPoint, Double>? {
        var nearest: ConnectionPoint? = null
        var distance = Double.MAX_VALUE

        children.forEach { child ->
            if (child !== exclude) {
                child.connectionPoints.forEach { cp ->
                    val d = (child.fromLocalToPage.value * cp.point.value - atPagePoint).length().inDefaultUnits
                    if (d < distance) {
                        nearest = cp
                        distance = d
                    }
                }
            }
        }

        if (nearest == null) {
            return null
        } else {
            return Pair(nearest!!, distance)
        }
    }

    /**
     * Finds the nearest geometry that a point can be connected to.
     * Returns the Geometry, the distance and the amount along.
     */
    fun findNearestConnectionGeometry(atPagePoint: Dimension2, exclude: Shape): Triple<Shape, Double, Double>? {
        var nearest: Shape? = null
        var nearestDistance = Double.MAX_VALUE
        var nearestAlong = 0.0

        children.forEach { child ->
            if (child !== exclude) {
                val localPoint = child.fromPageToLocal.value * atPagePoint
                // Ignore geometries that cannot be connected to.
                if (child.geometry.connect.value) {

                    val nonMoveCount = child.geometry.parts.count { it !is MoveTo }
                    val interval = 1.0 / nonMoveCount
                    var moveToCount = 0

                    child.geometry.parts.forEachIndexed { index, part ->
                        if (part is MoveTo) {
                            moveToCount++
                        } else {

                            part.checkAlong(child, localPoint)?.let { (dist, along) ->
                                if (dist < nearestDistance) {
                                    nearest = child
                                    nearestDistance = dist
                                    nearestAlong = ((index - moveToCount) + along) * interval
                                }
                            }
                        }
                    }
                }
            }
        }

        if (nearest == null) {
            return null
        } else {
            return Triple(nearest!!, nearestDistance, nearestAlong)
        }
    }

    val fromLocalToParent: Prop<Matrix33>

    val fromParentToLocal: Prop<Matrix33>

    val fromLocalToPage: Prop<Matrix33>

    val fromPageToLocal: Prop<Matrix33>

}
