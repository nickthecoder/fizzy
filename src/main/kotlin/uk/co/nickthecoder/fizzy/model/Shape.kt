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
import uk.co.nickthecoder.fizzy.evaluator.CompoundEvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.*

abstract class Shape(var parent: Parent)
    : Parent, PropListener, HasChangeListeners<Shape> {

    val id = PropConstant(parent.document().generateId())

    val name = StringExpression("\"\"")

    // Note, this is abstract to avoid leaking 'this' in the constructor, so it is only instantiated in final sub-classes
    abstract val context: EvaluationContext

    // Note, this is abstract to avoid leaking 'this' in the constructor, so it is only instantiated in final sub-classes
    abstract val transform: ShapeTransform

    override var listeners = ChangeListeners<Shape>()

    final override val children = MutableFList<Shape>()

    override val fromLocalToParent
        get() = transform.fromLocalToParent

    override val fromParentToLocal
        get() = transform.fromParentToLocal

    override val fromLocalToPage: Prop<Matrix33>
        get() = transform.fromLocalToPage

    override val fromPageToLocal: Prop<Matrix33>
        get() = transform.fromPageToLocal

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
        listenTo(name)
        children.listeners.add(shapeListener)
        parent.children.add(this)
    }

    protected fun createContext(thisContext: ThisContext<*>) = CompoundEvaluationContext(listOf(
            constantsContext, thisContext))

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

    fun findShape(id: Int): Shape? {
        if (id == this.id.value) return this

        children.forEach { child ->
            val found = child.findShape(id)
            if (found != null) return found
        }
        return null
    }

    override fun findShape(name: String): Shape? {
        if (name == this.name.value) return this

        children.forEach { child ->
            val found = child.findShape(name)
            if (found != null) return found
        }
        return null
    }


    /**
     * point should be in units of the parent (i.e. in the same units as this.transform.pin).
     * It is NOT in the coordinates used by the [Geometry] sections.
     * Therefore we first need to convert it to this shapes coordinates, and then compare it to the
     * [Geometry].
     *
     * Returns true iff this geometry is close to the given point, or if any of the descendants isAt the point.
     *
     */
    open fun isAt(point: Dimension2): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        children.forEach { child ->
            if (child.isAt(localPoint)) {
                return true
            }
        }

        return false
    }

    /**
     * Listens to the expression, so that when it changes, Shape's listeners are informed.
     * The expression's [EvaluationContext] is also set.
     */
    protected fun listenTo(expression: PropExpression<*>) {
        expression.listeners.add(this)
        expression.context = context
    }

    override fun toString(): String = "Shape ${id.value}"

    companion object {

        fun createBox(parent: Parent, size: String, at: String, fill: Boolean = false): Shape2d {

            val box = Shape2d.create(parent)
            box.size.expression = size
            box.transform.pin.expression = at

            val geometry = Geometry()
            box.geometries.add(geometry)
            geometry.parts.add(MoveTo("Size * Vector2(0,0)"))
            geometry.parts.add(LineTo("Size * Vector2(1,0)"))
            geometry.parts.add(LineTo("Size * Vector2(1,1)"))
            geometry.parts.add(LineTo("Size * Vector2(0,1)"))
            geometry.parts.add(LineTo("Geometry1.Point1"))

            if (fill) {
                geometry.fill.expression = "true"
            }

            return box
        }

        // TODO We need to do something more sensible for the lineWidth.
        // Maybe when styles are in place, it will use a default style, rather than a hard coded value.
        fun createLine(parent: Parent, start: String, end: String, lineWidth: String = "2mm"): Shape1d {
            val line = Shape1d.create(parent)

            line.start.expression = start
            line.end.expression = end
            line.lineWidth.expression = lineWidth

            val geometry = Geometry()
            line.geometries.add(geometry)

            geometry.parts.add(MoveTo("Dimension2(0mm,LineWidth/2)"))
            geometry.parts.add(LineTo("Dimension2(Length,LineWidth/2)"))

            return line
        }
    }

}


/**
 * The basis for Shape1d and Shape2d, i.e. the type of Shapes which have their own Geometries.
 */
abstract class RealShape(parent: Parent) : Shape(parent) {

    val geometries = MutableFList<Geometry>()

    val lineWidth = DimensionExpression("2mm")


    private val geometriesListener by lazy {
        // lazy to prevent leaking this in the constructor.
        // NOTE. I tried just creating this in postInit (without a val), and I got a failed unit test
        // This only happened from within IntelliJ (running directly from gradle, all tests passed).
        // It also passed when running the single Test class (and also a single method).
        // However, I'm not in the mood for a bug hunt, so I'll leave this implementation here.
        // It is slightly weird looking, but it works!
        ChangeAndCollectionListener(this, geometries,
                onAdded = { geometry -> geometry.shape = this@RealShape },
                onRemoved = { geometry -> geometry.shape = null }
        )
    }

    override fun isAt(point: Dimension2): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        geometries.forEach { geo ->
            if (geo.isAt(localPoint, lineWidth.value)) {
                return true
            }
        }

        return super.isAt(point)
    }

    override fun postInit() {
        listenTo(lineWidth)
        geometriesListener // Force it to be initialised (it is by lazy).
        super.postInit()
    }

}
