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
import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.BooleanExpression
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class Geometry

    : HasChangeListeners<Geometry> {

    override val listeners = ChangeListeners<Geometry>()

    var shape: RealShape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    fill.listeners.remove(it)
                    line.listeners.add(it)
                }

                field = v

                parts.forEach { part ->
                    val context = v?.context ?: constantsContext
                    part.setContext(context)
                    fill.context = context
                    line.context = context
                    if (v != null) {
                        fill.listeners.add(v)
                        line.listeners.add(v)
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
                if (first != last && isCrossing(point, last, first)) {
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

/**
 * Tests if a horizontal ray through line crosses the line segment from prev to next.
 * See https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
 */
private fun isCrossing(here: Dimension2, prev: Dimension2, next: Dimension2): Boolean {
    val testx = here.x.inDefaultUnits
    val testy = here.y.inDefaultUnits
    return ((next.y.inDefaultUnits > testy) != (prev.y.inDefaultUnits > testy)) &&
            (testx < (prev.x.inDefaultUnits - next.x.inDefaultUnits) * (testy - next.y.inDefaultUnits)
                    / (prev.y.inDefaultUnits - next.y.inDefaultUnits) + next.x.inDefaultUnits)
}

abstract class GeometryPart

    : HasChangeListeners<GeometryPart>, PropListener {

    internal var geometry: Geometry? = null
        set(v) {
            field = v
            if (v == null) {
                setContext(constantsContext)
            } else {
                v.shape?.let { setContext(it.context) }
            }
        }

    /**
     * The end point for this geometery point.
     */
    abstract val point: Dimension2Expression

    internal abstract fun setContext(context: EvaluationContext)

    abstract fun expression(): String

    override val listeners = ChangeListeners<GeometryPart>()

    override fun dirty(prop: Prop<*>) {
        listeners.fireChanged(this, ChangeType.CHANGE, prop)
    }

    /**
     * Does the point touch the line given by this part of the geometry.
     * The thickness is the distance away [here] can be, and still be considered touching.
     * [here] is usually the shapes lineWidth, or greater if that is too thin.
     */
    abstract fun isAlong(here: Dimension2, prev: Dimension2, thickness: Dimension): Boolean

    abstract fun isCrossing(here: Dimension2, prev: Dimension2): Boolean

    abstract fun pointAlong(prev: Dimension2, along: Double): Dimension2
}

class MoveTo(expression: String = "Dimension2(0mm, 0mm)")
    : GeometryPart() {

    override val point = Dimension2Expression(expression)

    init {
        point.listeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }

    override fun expression() = "LineTo point='${point.expression}'"

    override fun isAlong(here: Dimension2, prev: Dimension2, thickness: Dimension) = false

    override fun isCrossing(here: Dimension2, prev: Dimension2) = false

    override fun pointAlong(prev: Dimension2, along: Double) = Dimension2.ZERO_mm

    override fun toString() = "MoveTo point=${point.value}"
}

class LineTo(expression: String = "Dimension2(0mm, 0mm)")

    : GeometryPart() {

    override val point = Dimension2Expression(expression)

    init {
        point.listeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }

    override fun expression() = "LineTo point='${point.expression}'"

    override fun isAlong(here: Dimension2, prev: Dimension2, thickness: Dimension): Boolean {

        // Using the following diagram given by Henry. A is prev, B is myPoint and C is here :
        // https://math.stackexchange.com/questions/60070/checking-whether-a-point-lies-on-a-wide-line-segment
        // We then use the formula given by "Did".

        val myPoint = point.value
        // The distance between this point and the previous point
        val dx = myPoint.x.inDefaultUnits - prev.x.inDefaultUnits
        val dy = myPoint.y.inDefaultUnits - prev.y.inDefaultUnits

        // The distance from the point being tested and the previous point.
        val dx2 = here.x.inDefaultUnits - prev.x.inDefaultUnits
        val dy2 = here.y.inDefaultUnits - prev.y.inDefaultUnits
        // (here-prev) dot-product (myPoint-prev)
        // -30 * 20 + 0 * 0

        val dotProduct = dx2 * dx + dy2 * dy

        // Is it beyond the line segment's ends?
        if (dotProduct < 0) return false
        val lengthSquared = dx * dx + dy * dy
        if (dotProduct > lengthSquared) return false

        val length2Squared = dx2 * dx2 + dy2 * dy2

        // Is it within the thickness of the line?
        return (lengthSquared * length2Squared) <= thickness.inDefaultUnits * thickness.inDefaultUnits + dotProduct * dotProduct
    }

    /**
     * Adapted from :
     * https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
     *
     * vert[i] = myPoint, vert[j] = prev, test = here
     */
    override fun isCrossing(here: Dimension2, prev: Dimension2) = isCrossing(here, prev, point.value)

    override fun pointAlong(prev: Dimension2, along: Double) = prev + (point.value - prev) / along

    override fun toString() = "LineTo point=${point.value}"
}
