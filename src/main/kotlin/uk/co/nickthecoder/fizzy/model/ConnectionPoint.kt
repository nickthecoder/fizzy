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
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class ConnectionPoint(point: String, angle: String) {

    val point = Dimension2Expression(point)

    /**
     * The preferred angle of lines coming out of this ConnectionPoint.
     */
    val direction = AngleExpression(angle)

    var shape: Shape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    point.propListeners.remove(it)
                    direction.propListeners.remove(it)
                }
                field = v

                val context = v?.context ?: constantsContext
                point.context = context
                direction.context = context

                if (v != null) {
                    point.propListeners.add(v)
                    direction.propListeners.add(v)
                }
            }
        }

}

class ConnectionPointProp(connectionPoint: ConnectionPoint)
    : PropValue<ConnectionPoint>(connectionPoint), PropListener,
        HasChangeListeners<ConnectionPointProp> {

    override val changeListeners = ChangeListeners<ConnectionPointProp>()

    init {
        connectionPoint.point.propListeners.add(this)
        connectionPoint.direction.propListeners.add(this)
    }

    /**
     * Any changes to the ConnectionPoint's data causes this [Prop]'s propListeners to be notified.
     * The [ConnectionPointProp]'s constructor adds itself to the listeners of each of [ConnectionPoint]'s [Prop]s.
     */
    override fun dirty(prop: Prop<*>) {
        propListeners.fireDirty(this)
    }
}
