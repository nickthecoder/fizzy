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

import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class ControlPoint(val point: Dimension2Expression)
    : HasChangeListeners<ControlPoint>, PropListener, MetaDataAware {

    constructor(pointFormula: String) : this(Dimension2Expression(pointFormula))

    constructor(pointValue: Dimension2) : this(Dimension2Expression(pointValue))

    constructor() : this(Dimension2Expression(Dimension2.ZERO_mm))


    override val changeListeners = ChangeListeners<ControlPoint>()


    var shape: Shape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    point.propListeners.remove(it)
                }
                field = v

                val context = v?.context ?: constantsContext
                point.context = context

                if (v != null) {
                    point.propListeners.add(v)
                }
            }
        }

    init {
        point.propListeners.add(this)
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this)
    }


    override fun metaData(): MetaData {
        val md = MetaData(null)
        addMetaData(md)
        return md
    }

    fun addMetaData(metaData: MetaData) {
        metaData.newCell("Point", point)
    }

    fun index(): Int {
        shape?.let { shape ->
            shape.controlPoints.forEachIndexed { index, cp ->
                if (cp === this) {
                    return index
                }
            }
        }
        return -1
    }

    /**
     * When dragging a [ControlPoint] to [localPoint], this returns a point that is suitable for the type of
     * control point. For a "free" control point, then [localPoint] is returned.
     * (constraints haven't been implemented yet!)
     */
    fun constrain(localPoint: Dimension2): Dimension2 = localPoint

    fun copy(link: Boolean) = ControlPoint(point.copy(link))
}
