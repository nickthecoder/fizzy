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
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.MetaData
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.prop.PropValue
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.HasChangeListeners


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
     * The end point for this geometry point.
     */
    abstract val point: Dimension2Expression

    open fun addMetaData(list: MutableList<MetaData>, sectionIndex: Int, rowIndex: Int) {
        list.add(MetaData("point", point, "Geometry", sectionIndex, rowIndex))
    }

    internal abstract fun setContext(context: EvaluationContext)

    abstract fun formula(): String

    override val changeListeners = ChangeListeners<GeometryPart>()

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this, ChangeType.CHANGE, prop)
    }

    /**
     * Does the point touch the line given by this part of the geometry.
     * The thickness is the distance away [here] can be, and still be considered touching.
     * [here] is usually the shapes lineWidth, or greater if that is too thin.
     */
    abstract fun isAlong(shape: Shape?, here: Dimension2, prev: Dimension2, lineWidth: Dimension, minDistance: Dimension): Boolean

    abstract fun isCrossing(here: Dimension2, prev: Dimension2): Boolean

    abstract fun pointAlong(prev: Dimension2, along: Double): Dimension2

    abstract fun copy(): GeometryPart

    companion object {

        /**
         * Tests if a horizontal ray through line crosses the line segment from prev to next.
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
