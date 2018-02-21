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

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.runLater

abstract class Shape(var parent: Parent)
    : PropListener, HasChangeListeners<Shape> {

    var id = PropConstant(parent.page().generateId())

    abstract val context: Context

    override var listeners = ChangeListeners<Shape>()

    val geometry = Geometry(this)

    val geometryListener = object : ChangeListener<Geometry> {
        override fun changed(item: Geometry, changeType: ChangeType, obj: Any?) {
            dirty = true
        }
    }

    init {
        id.listeners.add(this)
        parent.children.add(this)
        geometry.listeners.add(geometryListener)
    }

    private var dirty = false
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    runLater {
                        dirty = false
                        listeners.fireChanged(this)
                    }
                }
            }
        }

    override fun dirty(prop: Prop<*>) {
        dirty = true
    }

    fun page(): Page = parent.page()

    fun layer(): Layer = parent.layer()

    override fun toString(): String = "Shape ${id.value}"

}
