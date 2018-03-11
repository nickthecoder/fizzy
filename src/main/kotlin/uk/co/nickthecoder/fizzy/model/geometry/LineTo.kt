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
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression


class LineTo(override val point: Dimension2Expression)

    : GeometryPart() {

    constructor(pointFormula: String) : this(Dimension2Expression(pointFormula))

    constructor(point: Dimension2) : this(point.toFormula())

    init {
        point.propListeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }

    override fun isAlong(shape: Shape?, here: Dimension2, prev: Dimension2, lineWidth: Dimension, minDistance: Dimension): Boolean {

        // Using the following diagram given by Henry. A is prev, B is myPoint and C is here :
        // https://math.stackexchange.com/questions/60070/checking-whether-a-point-lies-on-a-wide-line-segment
        // We then use the formula given by "Did".

        val myPoint = point.value
        // The distance between this point and the previous point
        val ABx = myPoint.x.inDefaultUnits - prev.x.inDefaultUnits
        val ABy = myPoint.y.inDefaultUnits - prev.y.inDefaultUnits

        // The distance from the point being tested and the previous point.
        val ACx = here.x.inDefaultUnits - prev.x.inDefaultUnits
        val ACy = here.y.inDefaultUnits - prev.y.inDefaultUnits
        // (here-prev) dot-product (myPoint-prev)
        // -30 * 20 + 0 * 0

        val ACdotAB = ACx * ABx + ACy * ABy // The length of AD

        // Is it beyond the line segment's ends?
        if (ACdotAB < 0) return false
        val ABsquared = ABx * ABx + ABy * ABy
        if (ACdotAB > ABsquared) return false

        val ACsquared = ACx * ACx + ACy * ACy

        // Is it within the thickness of the line?
        if ((ABsquared * ACsquared) <= lineWidth.inDefaultUnits * lineWidth.inDefaultUnits + ACdotAB * ACdotAB) {
            return true
        }

        // But what if the lineWidth is very narrow. We need to compare the vector DC (in the diagram linked above)
        // with the given minThickness then DC is transformed to page coordinates.

        if (shape == null) return false

        // What follows is my own invention, and is probably very inefficient.
        val AB2 = ABx * ABx + ABy * ABy // The square of the length AB.
        val AD2 = ACdotAB * ACdotAB / AB2 // Use the dot product from earlier to find the length AD (squared).
        val DCsquared = ACsquared - AD2 // Using pythagoras we have the distance of the point from the line.
        val vDCsquared = Vector2(ABy, ABx).normalise() * DCsquared * Dimension(1.0) // Turn this into a vector in the direction of DC

        // Now we take (0,0) on the page, and convert it to local coordinates.
        // We add the local vector vDCsquared, and then convert it back into page coordinates.
        val onPage2 = (shape.fromLocalToPage.value * (shape.fromPageToLocal.value * Dimension2.ZERO_mm + vDCsquared)).length()
        // If the length of this vector is less than the minimum distance, we must have been close enough to the line.
        return Math.abs(onPage2.inDefaultUnits) < minDistance.inDefaultUnits * minDistance.inDefaultUnits

    }

    override fun checkAlong(shape: Shape, here: Dimension2, prev: Dimension2): Pair<Double, Double>? {

        // Using the following diagram given by Henry. A is prev, B is myPoint and C is here :
        // https://math.stackexchange.com/questions/60070/checking-whether-a-point-lies-on-a-wide-line-segment
        // We then use the formula given by "Did".

        val myPoint = point.value
        // The distance between this point and the previous point
        val ABx = myPoint.x.inDefaultUnits - prev.x.inDefaultUnits
        val ABy = myPoint.y.inDefaultUnits - prev.y.inDefaultUnits

        // The distance from the point being tested and the previous point.
        val ACx = here.x.inDefaultUnits - prev.x.inDefaultUnits
        val ACy = here.y.inDefaultUnits - prev.y.inDefaultUnits
        // (here-prev) dot-product (myPoint-prev)
        // -30 * 20 + 0 * 0

        val ACdotAB = ACx * ABx + ACy * ABy // The length of AD

        // Is it beyond the line segment's ends?
        if (ACdotAB < 0) return null
        val ABsquared = ABx * ABx + ABy * ABy
        if (ACdotAB > ABsquared) return null

        val ACsquared = ACx * ACx + ACy * ACy

        // But what if the lineWidth is very narrow. We need to compare the vector DC (in the diagram linked above)
        // with the given minThickness then DC is transformed to page coordinates.

        // What follows is my own invention, and is probably very inefficient.
        val AB2 = ABx * ABx + ABy * ABy // The square of the length AB.
        val AD2 = ACdotAB * ACdotAB / AB2 // Use the dot product from earlier to find the length AD (squared).
        val DCsquared = ACsquared - AD2 // Using pythagoras we have the distance of the point from the line.
        val vDC = Vector2(ABy, ABx).normalise() * Math.sqrt(DCsquared) * Dimension(1.0) // Turn this into a vector in the direction of DC

        // Now we take (0,0) on the page, and convert it to local coordinates.
        // We add the local vector vDCsquared, and then convert it back into page coordinates.
        val onPage = (shape.fromLocalToPage.value * (shape.fromPageToLocal.value * Dimension2.ZERO_mm + vDC)).length()
        val ratio = Math.sqrt(AD2) / Math.sqrt(AB2)

        /*
        println("Here $here compare to $prev  ->  $myPoint")
        println("AB = $ABx,$ABy AC = $ACx , $ACy")
        println("ACdotAB = $ACdotAB  AB2 = $AB2  DCsquared = $DCsquared")
        println("ACsquared = $ACsquared  AD2 = $AD2  DCsquared = $DCsquared  vDC = $vDC")
        println( "onPage = $onPage, ratio = $ratio")
        println("\n")
        */
        return Pair(onPage.inDefaultUnits, ratio)
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

    override fun copy(link: Boolean): GeometryPart = LineTo(point.copy(link))

    override fun toString() = "LineTo point=${point.value}"
}
