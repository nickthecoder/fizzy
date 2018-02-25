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
import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.runLater

abstract class Shape(var parent: Parent)
    : Parent, PropListener, HasChangeListeners<Shape> {

    var id = PropConstant(parent.document().generateId())

    abstract val context: EvaluationContext

    override var listeners = ChangeListeners<Shape>()

    final override val children = MutableFList<Shape>()

    abstract val transform: ShapeTransform

    override val transformation
        get() = transform.transformation

    private val shapeListener = object : ChangeListener<Shape>, CollectionListener<Shape> {

        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            listeners.fireChanged(this@Shape)
        }

        override fun added(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@Shape)
            item.listeners.add(this)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@Shape)
            item.listeners.add(this)
        }
    }

    /**
     * Shapes should not be created directly with a constructor, instead use a static 'create' method,
     * which calls [postInit]. This is to ensure that 'this' is not leaked from the constructor.
     */
    open protected fun postInit() {
        id.listeners.add(this)
        children.listeners.add(shapeListener)
        parent.children.add(this)
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

    override fun document(): Document = parent.document()

    override fun page(): Page = parent.page()

    fun findShape(id: String): Shape? {
        if (id == this.id.value) return this

        children.forEach { child ->
            val found = child.findShape(id)
            if (found != null) return found
        }
        return null
    }

    override fun toString(): String = "Shape ${id.value}"
}


/**
 * The basis for Shape1d and Shape2d, i.e. the type of Shapes which have their own Geometries.
 */
abstract class RealShape(parent: Parent) : Shape(parent) {

    val geometries = MutableFList<Geometry>()

    private val geometriesListener by lazy {
        // lazy to prevent leaking this in the constructor.
        // NOTE. I tried just creating this in postInit (without a val), and I got a failed unit test
        // This only happened from within IntelliJ (running directly from gradle, all tests passed).
        // It also passed when running the single Test class (and also a single method).
        // However, I'm not in the mood for a bug hunt, so I'll leave this implementation here.
        // It is slightly weird looking, but it works!
        object : ChangeAndCollectionListener<Shape, Geometry>(this, geometries) {

            override fun added(collection: FCollection<Geometry>, item: Geometry) {
                super.added(collection, item)
                item.shape = this@RealShape
            }

            override fun removed(collection: FCollection<Geometry>, item: Geometry) {
                super.removed(collection, item)
                item.shape = null
            }
        }
    }

    override fun postInit() {
        geometriesListener // Force it to be initialised (it is by lazy).
        super.postInit()
    }
}
