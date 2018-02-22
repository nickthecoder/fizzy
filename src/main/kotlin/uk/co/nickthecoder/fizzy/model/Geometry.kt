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

import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener

class Geometry()

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

    val lineWidth = DimensionExpression("1mm")

    private val geometryPartsListener = object : ChangeAndCollectionListener<Geometry, GeometryPart>(this, parts) {
        override fun added(collection: FCollection<GeometryPart>, item: GeometryPart) {
            super.added(collection, item)
            item.geometry = parent
        }

        override fun removed(collection: FCollection<GeometryPart>, item: GeometryPart) {
            super.removed(collection, item)
            item.geometry = null
        }
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

    internal abstract fun setContext(context: EvaluationContext)

    override val listeners = ChangeListeners<GeometryPart>()

    override fun dirty(prop: Prop<*>) {
        listeners.fireChanged(this, ChangeType.CHANGE, prop)
    }
}

class MoveTo()

    : GeometryPart() {

    val point = Dimension2Expression("Dimension2(0mm, 0mm)")

    init {
        point.listeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }
}

class LineTo()

    : GeometryPart() {

    val point = Dimension2Expression("Dimension2(0mm, 0mm)")

    init {
        point.listeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }
}
