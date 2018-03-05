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
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.*

abstract class Shape(var parent: ShapeParent)
    : ShapeParent, PropListener, HasChangeListeners<Shape> {

    val id = PropConstant(parent.document().generateId())

    val name = PropVariable("")

    // Note, this is abstract to avoid leaking 'this' in the constructor, so it is only instantiated in final sub-classes
    abstract val context: EvaluationContext

    // Note, this is abstract to avoid leaking 'this' in the constructor, so it is only instantiated in final sub-classes
    abstract val transform: ShapeTransform

    override var changeListeners = ChangeListeners<Shape>()

    final override val children = MutableFList<Shape>()


    override val fromLocalToParent
        get() = transform.fromLocalToParent

    override val fromParentToLocal
        get() = transform.fromParentToLocal

    override val fromLocalToPage: Prop<Matrix33>
        get() = transform.fromLocalToPage

    override val fromPageToLocal: Prop<Matrix33>
        get() = transform.fromPageToLocal

    /**
     * When child Shapes change, this causes that even to bubble up to listeners of this Shape.
     */
    private val shapeListener = object : ChangeListener<Shape>, CollectionListener<Shape> {

        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            changeListeners.fireChanged(this@Shape)
        }

        override fun added(collection: FCollection<Shape>, item: Shape) {
            changeListeners.fireChanged(this@Shape)
            item.changeListeners.add(this)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            changeListeners.fireChanged(this@Shape)
            item.changeListeners.add(this)
        }
    }

    /**
     * Keeps references to [ChangeAndCollectionListener], so that they are not gc'd.
     * They do not need to be referenced directly, they take care of everything themselves.
     */
    protected val collectionListeners = mutableListOf<ChangeAndCollectionListener<Shape, *>>()

    /**
     * Shapes should not be created directly with a constructor, instead use a static 'create' method,
     * which calls [postInit]. This is to ensure that 'this' is not leaked from the constructor.
     */
    open protected fun postInit() {
        listenTo(name)
        children.listeners.add(shapeListener)
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
                        changeListeners.fireChanged(this)
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
    open fun isAt(point: Dimension2, minDistance: Dimension): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        children.forEach { child ->
            if (child.isAt(localPoint, minDistance)) {
                return true
            }
        }

        return false
    }

    /**
     * Listens to the expression, so that when it changes, Shape's listeners are informed.
     * The expression's [EvaluationContext] is also set.
     */
    protected fun listenTo(expression: Prop<*>) {
        expression.propListeners.add(this)
        if (expression is PropExpression<*>) {
            expression.context = context
        }
    }

    abstract fun copyInto(parent: ShapeParent): Shape

    open protected fun populateShape(newShape: Shape) {
        newShape.name.value = name.value
        newShape.transform.locPin.formula = transform.locPin.formula
        newShape.transform.rotation.formula = transform.rotation.formula
        newShape.transform.pin.formula = transform.pin.formula
        newShape.transform.scale.formula = transform.scale.formula

        children.forEach { it.copyInto(newShape) }
    }

    fun metaData(): List<MetaData> {
        val result = mutableListOf<MetaData>()
        addMetaData(result)
        return result
    }

    open protected fun addMetaData(list: MutableList<MetaData>) {
        list.add(MetaData("ID", DoubleExpression(id.toString())))
        list.add(MetaData("Name", StringExpression(name.value)))
        list.add(MetaData("Rotation", transform.rotation))
        list.add(MetaData("Pin", transform.pin))
        list.add(MetaData("LocPin", transform.locPin))
        list.add(MetaData("Scale", transform.scale))
    }

    override fun toString(): String = "Shape ${id.value}"

    companion object {

        fun createBox(
                parent: ShapeParent,
                size: String,
                at: String = "Dimension2(0mm,0mm)",
                fillColor: String? = "Color.white")
                : Shape2d {

            val box = Shape2d.create(parent)
            box.size.formula = size
            box.transform.pin.formula = at

            val geometry = Geometry()
            geometry.parts.add(MoveTo("Size * Vector2(0,0)"))
            geometry.parts.add(LineTo("Size * Vector2(1,0)"))
            geometry.parts.add(LineTo("Size * Vector2(1,1)"))
            geometry.parts.add(LineTo("Size * Vector2(0,1)"))
            geometry.parts.add(LineTo("Geometry1.Point1"))
            geometry.fill.formula = "true"
            geometry.connect.formula = "true" // Allow connections along
            box.addGeometry(geometry)

            if (fillColor != null) {
                box.fillColor.formula = fillColor
                geometry.fill.formula = "true"
            }

            return box
        }

        // TODO We need to do something more sensible for the lineWidth.
        // Maybe when styles are in place, it will use a default style, rather than a hard coded value.
        fun createLine(
                parent: ShapeParent,
                start: String,
                end: String,
                lineWidth: String = "2mm")
                : Shape1d {

            val line = Shape1d.create(parent)

            line.start.formula = start
            line.end.formula = end
            line.lineWidth.formula = lineWidth

            val geometry = Geometry()
            geometry.parts.add(MoveTo("Dimension2(0mm,LineWidth/2)"))
            geometry.parts.add(LineTo("Dimension2(Length,LineWidth/2)"))
            line.addGeometry(geometry)

            return line
        }

        fun createPolygon(
                parent: ShapeParent,
                sides: Int,
                radius: Dimension,
                star: Boolean = true,
                fillColor: String? = "Color.white",
                at: String = "Dimension2(0mm,0mm)")
                : Shape2d {

            val poly = Shape2d.create(parent)
            poly.transform.pin.formula = at

            val unit = Dimension2(radius, Dimension.ZERO_mm)
            val geometry = Geometry()
            poly.addGeometry(geometry)

            // Create points around (0,0)
            var first = true
            for (i in 0..sides - 1) {
                val point = unit.rotate(Angle.TAU * (i.toDouble() / sides))
                if (first) {
                    geometry.parts.add(MoveTo(point.toFormula()))
                    first = false
                } else {
                    geometry.parts.add(LineTo(point.toFormula()))
                }
            }

            val minX = geometry.parts.minBy { it.point.value.x }
            val minY = geometry.parts.minBy { it.point.value.y }

            val maxX = geometry.parts.maxBy { it.point.value.x }
            val maxY = geometry.parts.maxBy { it.point.value.y }

            if (minX != null && minY != null && maxX != null && maxY != null) {

                val shapeSize =
                        Dimension2(maxX.point.value.x, maxY.point.value.y) -
                                Dimension2(minX.point.value.x, minY.point.value.y)

                poly.size.formula = shapeSize.toFormula()

                // Translate them, so they are all +ve
                // Also, make them reference Size, so that resizing works as expected.
                val translate = Dimension2(-minX.point.value.x, -minY.point.value.y)
                geometry.parts.forEach { part ->
                    val ratio = (part.point.value + translate).ratio(shapeSize)
                    part.point.formula = "Size * ${ratio.toFormula()}"
                }
                poly.transform.locPin.formula = "Size * ${(translate.ratio(shapeSize)).toFormula()}"
            }

            if (star) {
                // Create connection points at each vertex
                for (i in 0..sides - 1) {
                    val cp = ConnectionPoint("Geometry1.Point${i + 2}", "0deg")
                    poly.addConnectionPoint(cp)
                }

                // Add extra geometries for the inner vertices of the star
                for (i in 0..sides - 1) {
                    geometry.parts.add(i * 2 + 1, LineTo("LocPin + ( (ControlPoint.Point1 - LocPin) / Size).rotate( $i / $sides * TAU rad ) * Size"))
                }

                // Create a control point between 1st two outer points
                poly.addControlPoint(ControlPoint("(Geometry1.Point1 + Geometry1.Point3)/2"))

            }

            // Now complete the job by adding a LineTo to the end
            geometry.parts.add(LineTo("Geometry1.Point1"))

            if (fillColor != null) {
                poly.fillColor.formula = fillColor
                geometry.fill.formula = "true"
            }

            //println(poly.metaData().joinToString(separator = "\n"))
            return poly
        }
    }

}
