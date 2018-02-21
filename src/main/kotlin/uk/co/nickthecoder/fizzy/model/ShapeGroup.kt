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

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.CompoundContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.ShapeGroupPropType

class ShapeGroup(parent: Parent)

    : Shape(parent), Parent {

    override val context = CompoundContext(listOf(
            constantsContext, ThisContext(PropConstant(this), ShapeGroupPropType.instance)))

    override var children = MutableFList<Shape>()


    private val shapeListener = object : ChangeListener<Shape>, CollectionListener<Shape> {

        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            listeners.fireChanged(this@ShapeGroup)
        }

        override fun added(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@ShapeGroup)
            item.listeners.add(this)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@ShapeGroup)
            item.listeners.add(this)
        }
    }

    init {
        children.listeners.add(shapeListener)
    }

}
