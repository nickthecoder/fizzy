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
import uk.co.nickthecoder.fizzy.prop.PropValue
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class ConnectionPoint(val point: Dimension2Expression) {

    constructor(pointFormula: String) : this(Dimension2Expression(pointFormula))

    var shape: RealShape? = null
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

    fun addMetaData(list: MutableList<MetaData>, index: Int) {
        list.add(MetaData("Point", point, "ConnectionPoint", index))
    }

    fun index(): Int {
        shape?.let { shape ->
            shape.connectionPoints.forEachIndexed { index, prop ->
                val cp = prop.value
                if (cp === this) {
                    return index
                }
            }
        }
        return -1
    }

    fun connectToFormula() = shape?.let { "connectTo( Page.Shape${it.id.value}.ConnectionPoint${index() + 1} )" }

    fun copy(link: Boolean) = ConnectionPoint(point.copy(link))

}

class ConnectionPointProp(connectionPoint: ConnectionPoint)
    : PropValue<ConnectionPoint>(connectionPoint),
        PropListener,
        HasChangeListeners<ConnectionPointProp> {

    override val changeListeners = ChangeListeners<ConnectionPointProp>()

    init {
        connectionPoint.point.propListeners.add(this)
    }

    /**
     * Any changes to the ConnectionPoint's data causes this [Prop]'s propListeners to be notified.
     * The [ConnectionPointProp]'s constructor adds itself to the listeners of each of [ConnectionPoint]'s [Prop]s.
     */
    override fun dirty(prop: Prop<*>) {
        propListeners.fireDirty(this)
    }
}
