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
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.prop.PropValue
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.HasChangeListeners


abstract class GeometryPart

    : HasChangeListeners<GeometryPart>, PropListener, MetaDataAware {

    internal var geometry: Geometry? = null
        set(v) {
            field = v
            if (v == null) {
                setContext(constantsContext)
            } else {
                setContext(v.shape.context)
            }
        }

    /**
     * The end point for this geometry part (the start of a line will be the previous GeometryPart's point).
     */
    abstract val point: Dimension2Expression

    override fun metaData(): MetaData {
        val metaData = MetaData(null)
        addMetaData(metaData)
        return metaData
    }

    open fun addMetaData(metaData: MetaData) {
        metaData.cells.add(MetaDataCell("Point", point))
    }

    internal abstract fun setContext(context: EvaluationContext)

    override val changeListeners = ChangeListeners<GeometryPart>()

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this, ChangeType.CHANGE, prop)
    }

    /**
     * Does the point touch the line given by this part of the geometry.
     * Note that [lineWidth] is in local coordinate system, whereas [minDistance] is in the page's coordinate system.
     * This lets us check if the point is within the thickness of the line and also check if the point is
     * "close enough", based on the zoom level of the view (even when the line width is very thin at that zoom level).
     *
     * @param here The point to test, in local coordinates.
     * @param prev The end point of the previous GeometryPart (and therefore the start of this one).
     * @param lineWidth The thickness of the line in local coordinates
     * @param minDistance The distance in page coordinates that can also be considered touching (used when lineWidth is thin).
     */
    abstract fun isAlong(shape: Shape?, here: Dimension2, prev: Dimension2, lineWidth: Dimension, minDistance: Dimension): Boolean

    /**
     * If the point is not along the line (e.g. it is passed the start/end points), then null is returned.
     * This is similar to [isAlong] returning false.
     * For non-null return values, return the distance away from the line, and the ratio of how far along the line.
     *
     * The distance is the minimum distance away from the line/curve in the page's coordinates.
     * The ratio should be in the range 0..1 and should be suitable to be passed into [pointAlong] for the 'along' parameter.
     *
     * @param here The point to test, in local coordinate system
     * @param prev The end point of the previous GeometryPart (and therefore the start of this one).
     * @return distance in page coordinates , how far along the line in the range 0..1
     */
    abstract fun checkAlong(shape: Shape, here: Dimension2, prev: Dimension2): Pair<Double, Double>?

    /**
     * Used to test if a point is withing a polygon.
     * Does a horizontal ray cross the line segment from [prev] to this.[point].
     */
    abstract fun isCrossing(here: Dimension2, prev: Dimension2): Boolean

    abstract fun pointAlong(prev: Dimension2, along: Double): Dimension2

    abstract fun copy(link: Boolean): GeometryPart

    companion object {

        /**
         * Tests if a horizontal ray crosses the line segment from prev to next.
         * See https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
         */
        fun isCrossing(here: Dimension2, prev: Dimension2, next: Dimension2): Boolean {
            val testx = here.x.inDefaultUnits
            val testy = here.y.inDefaultUnits
            return ((next.y.inDefaultUnits > testy) != (prev.y.inDefaultUnits > testy)) &&
                    (testx < (prev.x.inDefaultUnits - next.x.inDefaultUnits) * (testy - next.y.inDefaultUnits)
                            / (prev.y.inDefaultUnits - next.y.inDefaultUnits) + next.x.inDefaultUnits)
        }

    }
}

class GeometryPartProp(geometryPart: GeometryPart)
    : PropValue<GeometryPart>(geometryPart) {

}
