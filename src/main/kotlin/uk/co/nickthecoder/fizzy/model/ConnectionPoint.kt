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

class ConnectionPoint(val point: Dimension2Expression)
    : HasChangeListeners<ConnectionPoint>, PropListener, MetaDataAware {

    constructor(pointFormula: String) : this(Dimension2Expression(pointFormula))

    constructor() : this(Dimension2Expression(Dimension2.ZERO_mm))

    override val changeListeners = ChangeListeners<ConnectionPoint>()

    init {
        point.propListeners.add(this)
    }

    /**
     * Any changes to the ConnectionPoint's data causes this [Prop]'s propListeners to be notified.
     * The [ConnectionPointProp]'s constructor adds itself to the listeners of each of [ConnectionPoint]'s [Prop]s.
     */
    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this)
    }

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
            shape.connectionPoints.forEachIndexed { index, cp ->
                if (cp === this) {
                    return index
                }
            }
        }
        return -1
    }

    fun connectToFormula() = shape?.let { "connectTo( Page.Shape${it.id}.ConnectionPoint${index() + 1} )" }

    fun copy(link: Boolean) = ConnectionPoint(point.copy(link))

}
