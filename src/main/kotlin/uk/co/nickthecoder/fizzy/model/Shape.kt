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

    val id = parent.document().generateShapeId()

    val name = StringExpression("".toFormula())

    abstract val size: Dimension2Expression

    val lineWidth = DimensionExpression("2mm")

    val strokeCap = StrokeCapExpression(StrokeCap.ROUND.toFormula())

    val strokeJoin = StrokeJoinExpression(StrokeJoin.ROUND.toFormula())

    val strokeColor = PaintExpression("BLACK")

    val fillColor = PaintExpression("WHITE")


    // Note, this is abstract to avoid leaking 'this' in the constructor, so it is only instantiated in final sub-classes
    abstract val context: EvaluationContext

    // Note, this is abstract to avoid leaking 'this' in the constructor, so it is only instantiated in final sub-classes
    abstract val transform: ShapeTransform

    override var changeListeners = ChangeListeners<Shape>()

    final override val children = MutableFList<Shape>()


    val geometry = Geometry(this)

    val connectionPoints = MutableFList<ConnectionPoint>()

    val controlPoints = MutableFList<ControlPoint>()

    val scratches = ScratchList()


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
            changeListeners.fireChanged(this@Shape, ChangeType.ADD, item)
            item.changeListeners.add(this)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            changeListeners.fireChanged(this@Shape, ChangeType.REMOVE, item)
            item.changeListeners.add(this)
        }
    }

    val geometryListener = object : ChangeListener<Geometry> {
        override fun changed(item: Geometry, changeType: ChangeType, obj: Any?) {
            changeListeners.fireChanged(this@Shape, ChangeType.CHANGE, item)
        }
    }

    /**
     * Keeps references to [ChangeAndCollectionListener], so that they are not gc'd.
     * They do not need to be referenced directly, they take care of everything themselves.
     */
    protected val collectionListeners = mutableListOf<Any>()

    /**
     * Shapes should not be created directly with a constructor, instead use a static 'create' method,
     * which calls [postInit]. This is to ensure that 'this' is not leaked from the constructor.
     */
    open protected fun postInit() {
        children.listeners.add(shapeListener)
        geometry.changeListeners.add(geometryListener)

        listenTo(name, lineWidth, strokeCap, strokeJoin, strokeColor, fillColor)

        collectionListeners.add(ChangeAndCollectionListener(this, connectionPoints,
                onAdded = { item -> item.shape = this },
                onRemoved = { item -> item.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, controlPoints,
                onAdded = { item -> item.shape = this },
                onRemoved = { item -> item.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, scratches,
                onAdded = { item -> item.setContext(context) },
                onRemoved = { item -> item.setContext(constantsContext) }
        ))
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
        if (id == this.id) return this

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

    fun findShapeText(): ShapeText? {
        if (this is ShapeText) return this

        children.forEach { child ->
            if (child is ShapeText) {
                return child
            }
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
    open fun isAt(pagePoint: Dimension2, minDistance: Dimension): Boolean {
        val localPoint = transform.fromPageToLocal.value * pagePoint

        if (geometry.isAt(localPoint, lineWidth.value, minDistance)) {
            return true
        }

        children.forEach { child ->
            if (child.isAt(pagePoint, minDistance)) {
                return true
            }
        }
        return false
    }

    fun createText(text: String = "", alignX: Double = 0.5, alignY: Double = 0.5): ShapeText {
        val shapeText = Shape.createText(this, text, alignX = alignX, alignY = alignY)
        return shapeText
    }

    fun findScratch(name: String): Scratch? {
        scratches.forEach { scratchProp ->
            if (scratchProp.name.value == name) {
                return scratchProp
            }
        }
        return null
    }

    /**
     * Listens to the expression, so that when it changes, Shape's listeners are informed.
     * The expression's [EvaluationContext] is also set.
     */
    fun listenTo(vararg expressions: Prop<*>) {
        expressions.forEach { expression ->
            expression.propListeners.add(this)
            if (expression is PropExpression<*>) {
                expression.context = context
            }
        }
    }

    abstract fun copyInto(parent: ShapeParent, link: Boolean): Shape

    protected fun populateShape(newShape: Shape, link: Boolean) {

        geometry.copyInto(newShape, link)

        connectionPoints.forEach { connectionPoint ->
            newShape.connectionPoints.add(connectionPoint.copy(link))
        }
        controlPoints.forEach { controlPoint ->
            newShape.controlPoints.add(controlPoint.copy(link))
        }
        scratches.forEach { scratch ->
            newShape.scratches.add(scratch.copy(link))
        }

        metaData().copyInto(newShape.metaData(), link)
    }

    fun metaData(): MetaData {
        val result = MetaData()
        addMetaData(result)
        return result
    }

    fun debugCheckStale(): Boolean {
        var foundStale = false
        metaData().cells.forEach { cell ->
            val value = cell.cellExpression.value
            cell.cellExpression.forceRecalculation()
            if (cell.cellExpression.value != value) {
                foundStale = true
                println("Stale : $cell")
            }
        }
        return foundStale
    }

    open protected fun addMetaData(metaData: MetaData) {
        metaData.cells.add(MetaDataCell("Name", StringExpression(name.value.toFormula())))
        metaData.cells.add(MetaDataCell("Pin", transform.pin))
        metaData.cells.add(MetaDataCell("LocPin", transform.locPin))
        metaData.cells.add(MetaDataCell("Scale", transform.scale))

        geometry.addMetaData(metaData)
        connectionPoints.forEachIndexed { index, connectionPointProp -> connectionPointProp.addMetaData(metaData, index) }
        controlPoints.forEachIndexed { index, controlPointProp -> controlPointProp.addMetaData(metaData, index) }
        scratches.forEachIndexed { index, scratchProp -> scratchProp.addMetaData(metaData, index) }
        metaData.cells.add(MetaDataCell("LineWidth", lineWidth))
        metaData.cells.add(MetaDataCell("Size", size))
        metaData.cells.add(MetaDataCell("LineColor", strokeColor))
        metaData.cells.add(MetaDataCell("FillColor", fillColor))
        metaData.cells.add(MetaDataCell("StrokeCap", strokeCap))
        metaData.cells.add(MetaDataCell("StrokeJoin", strokeJoin))
    }

    override fun toString(): String = "Shape ${id}"

    companion object {

        fun createText(
                parent: ShapeParent,
                str: String = "",
                fontName: String = "Sans Regular",
                fontSize: String = "46pt",
                at: String = "Dimension2(0mm,0mm)",
                alignX: Double = 0.5,
                alignY: Double = 0.5
        ): ShapeText {

            val text = ShapeText.create(parent)
            text.text.formula = str.toFormula()
            text.fontSize.formula = fontSize
            text.fontName.formula = fontName.toFormula()
            text.fillColor.formula = "BLACK"
            text.alignX.formula = alignX.toFormula()
            text.alignY.formula = alignY.toFormula()

            if (parent is Shape2d) {
                text.size.formula = "Parent.Size - Dimension2( MarginLeft + MarginRight, MarginTop + MarginBottom )"
                text.transform.pin.formula = "Size * Vector2(AlignX, AlignY) + Dimension2( MarginTop, MarginLeft )"
                text.clip.formula = true.toFormula()
                //text.transform.locPin.formula = "Size / 2"
            } else {
                text.transform.pin.formula = at
            }

            return text
        }

        fun createBox(
                parent: ShapeParent,
                size: String = "Dimension2(100mm,100mm)",
                at: String = "Dimension2(0mm,0mm)",
                fillColor: String? = "WHITE")
                : Shape2d {

            val box = Shape2d.create(parent)
            box.size.formula = size
            box.transform.pin.formula = at

            box.geometry.parts.add(MoveTo("Size * Vector2(0,0) + Dimension2(LineWidth/2, LineWidth/2)"))
            box.geometry.parts.add(LineTo("Size * Vector2(1,0) + Dimension2(-LineWidth/2, LineWidth/2)"))
            box.geometry.parts.add(LineTo("Size * Vector2(1,1) + Dimension2(-LineWidth/2, -LineWidth/2)"))
            box.geometry.parts.add(LineTo("Size * Vector2(0,1) + Dimension2(LineWidth/2, -LineWidth/2)"))
            box.geometry.parts.add(LineTo("Geometry1.Point1"))
            box.geometry.fill.formula = "true"
            box.geometry.connect.formula = "true" // Allow connections along

            if (fillColor != null) {
                box.fillColor.formula = fillColor
                box.geometry.fill.formula = "true"
            }

            return box
        }

        // TODO We need to do something more sensible for the lineWidth.
        // Maybe when styles are in place, it will use a default style, rather than a hard coded value.
        fun createLine(
                parent: ShapeParent,
                start: String = "Dimension2(0mm,0mm)",
                end: String = "Dimension2(10mm,10mm)",
                lineWidth: String = "2mm")
                : Shape1d {

            val line = Shape1d.create(parent)

            line.start.formula = start
            line.end.formula = end
            line.lineWidth.formula = lineWidth

            line.geometry.parts.add(MoveTo("Dimension2(0mm,LineWidth/2)"))
            line.geometry.parts.add(LineTo("Dimension2(Length,LineWidth/2)"))

            return line
        }

        fun createPolygon(
                parent: ShapeParent,
                sides: Int,
                radius: Dimension = Dimension(60.0, Dimension.Units.mm),
                star: Boolean = false,
                fillColor: String? = "WHITE",
                at: String = "Dimension2(0mm,0mm)")
                : Shape2d {

            val poly = Shape2d.create(parent)
            poly.transform.pin.formula = at
            poly.transform.locPin.formula = Dimension2.ZERO_mm.toFormula()

            val unit = Dimension2(radius, Dimension.ZERO_mm)

            // Create points around (0,0)
            poly.geometry.parts.add(LineTo("Dimension2(${radius.toFormula()},0mm)"))
            for (i in 1..sides) {
                val point = unit.rotate(Angle.TAU * (i.toDouble() / sides))
                poly.geometry.parts.add(LineTo(point))
            }

            val minX = poly.geometry.parts.minBy { it.point.value.x }
            val minY = poly.geometry.parts.minBy { it.point.value.y }

            val maxX = poly.geometry.parts.maxBy { it.point.value.x }
            val maxY = poly.geometry.parts.maxBy { it.point.value.y }

            if (minX != null && minY != null && maxX != null && maxY != null) {

                val shapeSize =
                        Dimension2(maxX.point.value.x, maxY.point.value.y) -
                                Dimension2(minX.point.value.x, minY.point.value.y)

                poly.size.formula = shapeSize.toFormula()

                val translate = Dimension2(-minX.point.value.x, -minY.point.value.y)
                poly.scratches.add(Scratch("MagicRatio", Vector2Expression(
                        Vector2(1.0, (maxY.point.value.y - minY.point.value.y).ratio(maxX.point.value.x - minX.point.value.x)))))

                poly.transform.locPin.formula = "Size * ${translate.ratio(shapeSize).toFormula()}"

                // Translate them, so they are all +ve
                // Also, make them reference Size, so that resizing works as expected.
                poly.geometry.parts.forEach { part ->
                    val ratio = (part.point.value + translate).ratio(shapeSize)
                    part.point.formula = "Size * ${ratio.toFormula()}"
                }
            }

            if (star) {

                // Add extra geometries for the inner vertices of the star
                for (i in 0..sides - 1) {
                    poly.geometry.parts.add(i * 2 + 1, LineTo("LocPin + ( (ControlPoint.Point1 - LocPin) / Size * Scratch.MagicRatio ).rotate( ($i / $sides * TAU) rad ) * Size / Scratch.MagicRatio"))
                }

                // Create a control point between 1st two outer points
                poly.controlPoints.add(ControlPoint("(Geometry1.Point1 + Geometry1.Point3)/2"))

            }

            if (fillColor != null) {
                poly.fillColor.formula = fillColor
                poly.geometry.fill.formula = "true"
            }

            // Create connection points at each vertex
            for (i in 0..poly.geometry.parts.size - 2) {
                val cp = ConnectionPoint("Geometry1.Point${i + 1}")
                poly.connectionPoints.add(cp)
            }

            //println(poly.metaData().joinToString(separator = "\n"))
            return poly
        }
    }

}
