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

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression


class LineTo(formula: String = "Dimension2(0mm, 0mm)")

    : GeometryPart() {

    override val point = Dimension2Expression(formula)

    init {
        point.propListeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }

    override fun formula() = "LineTo point='${point.formula}'"

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

    override fun pointAlong(prev: Dimension2, along: Double): Dimension2 {
        return prev + (point.value - prev) * along
    }

    override fun toString() = "LineTo point=${point.value}"
}
