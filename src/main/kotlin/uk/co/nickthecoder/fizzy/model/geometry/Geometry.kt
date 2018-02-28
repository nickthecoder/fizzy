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
package uk.co.nickthecoder.fizzy.model.geometry

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.RealShape
import uk.co.nickthecoder.fizzy.prop.BooleanExpression
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class Geometry

    : HasChangeListeners<Geometry> {

    override val changeListeners = ChangeListeners<Geometry>()

    var shape: RealShape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    fill.propListeners.remove(it)
                    line.propListeners.add(it)
                }

                field = v

                parts.forEach { part ->
                    val context = v?.context ?: constantsContext
                    part.setContext(context)
                    fill.context = context
                    line.context = context
                    if (v != null) {
                        fill.propListeners.add(v)
                        line.propListeners.add(v)
                    }
                }
            }
        }

    var parts = MutableFList<GeometryPart>()

    val fill = BooleanExpression("false")
    val line = BooleanExpression("true")

    private val geometryPartsListener = ChangeAndCollectionListener(this, parts,
            onAdded = { part -> part.geometry = this },
            onRemoved = { part -> part.geometry = null }
    )

    fun isAt(point: Dimension2, thickness: Dimension): Boolean {

        var prev: Dimension2? = null

        // Adapted from : https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
        var oddEven = false
        if (fill.value && parts.size > 2) {
            parts.forEach { part ->
                prev?.let {
                    if (part.isCrossing(point, it)) {
                        oddEven = !oddEven
                    }
                }
                prev = part.point.value
            }
            // If the Geometry is not closed. i.e. if the first point isn't the same as the last point,
            // then add an extra line to close the shape.
            val first = parts[0].point.value
            val last = parts[parts.size - 1].point.value
            if (first != last) {
                if (first != last && GeometryPart.isCrossing(point, last, first)) {
                    oddEven = !oddEven
                }
            }
            if (oddEven) {
                return true
            }
        }

        if (line.value) {
            prev = null
            parts.forEach { part ->
                prev?.let {
                    if (part.isAlong(point, it, thickness)) {
                        return true
                    }
                }
                prev = part.point.value
            }
        }

        return false
    }

    /**
     * Find the point part way along this geometry. 0 will be the start of the geometry,
     * 1 will be the end.
     */
    fun pointAlong(along: Double): Dimension2 {
        val nonMoveCount = parts.count { it !is MoveTo }

        // Clip to 0..1
        val alongClipped = if (along < 0) {
            0.0
        } else if (along > 1.0) {
            1.0
        } else {
            along
        }

        var partIndex = Math.floor(nonMoveCount * alongClipped).toInt()
        val partAlong = alongClipped - partIndex / nonMoveCount

        var prev: Dimension2? = null
        for (part in parts) {
            if (partIndex == 0) {
                if (prev != null) {
                    return parts[partIndex].pointAlong(prev, partAlong)
                }
                break
            }
            prev = part.point.value
            partIndex--
        }
        return Dimension2.ZERO_mm
    }
}
