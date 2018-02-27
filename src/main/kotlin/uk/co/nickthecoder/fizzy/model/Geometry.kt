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
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener

class Geometry

    : HasChangeListeners<Geometry> {

    override val listeners = ChangeListeners<Geometry>()

    var shape: RealShape? = null
        set(v) {
            field = v
            parts.forEach { part ->
                part.setContext(v?.context ?: constantsContext)
            }
        }

    var parts = MutableFList<GeometryPart>()

    // TODO Add fill and line booleans

    private val geometryPartsListener = ChangeAndCollectionListener(this, parts,
            onAdded = { part -> part.geometry = this },
            onRemoved = { part -> part.geometry = null }
    )

    fun isAt(point: Dimension2, thickness: Dimension): Boolean {
        // TODO If this is filled, use part.isWithin rather than isAlong

        var prev: Dimension2? = null
        parts.forEach { part ->
            prev?.let {
                if (part.isAlong(point, it, thickness)) {
                    return true
                }
            }
            prev = part.point.value
        }
        return false
    }
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

    override fun toString() = "LineTo point=${point.value}"
}
