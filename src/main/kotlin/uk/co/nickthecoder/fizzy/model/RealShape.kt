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

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.model.geometry.GeometryProp
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.prop.PaintExpression
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener


/**
 * The basis for Shape1d and Shape2d, i.e. the type of Shapes which have Geometries, ConnectionPoints etc.
 */
abstract class RealShape(parent: ShapeParent)
    : Shape(parent) {

    val geometries = MutableFList<GeometryProp>()

    val connectionPoints = MutableFList<ConnectionPointProp>()

    val controlPoints = MutableFList<ControlPointProp>()

    val scratches = MutableFList<ScratchProp>()

    val lineWidth = DimensionExpression("2mm")

    abstract val size: Dimension2Expression

    val lineColor = PaintExpression("Color.black")

    val fillColor = PaintExpression("Color.white")

    override fun isAt(point: Dimension2, minDistance: Dimension): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        geometries.forEach { geo ->
            if (geo.value.isAt(localPoint, lineWidth.value, minDistance)) {
                return true
            }
        }

        return super.isAt(point, minDistance)
    }

    override fun postInit() {
        listenTo(lineWidth)
        listenTo(lineColor)
        listenTo(fillColor)
        super.postInit()
        // Automatically tell the child of the parent when it is added to the list (and set to null when removed)
        // Also bubble change events up the hierarchy.
        collectionListeners.add(ChangeAndCollectionListener(this, geometries,
                onAdded = { geometry -> geometry.value.shape = this },
                onRemoved = { geometry -> geometry.value.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, connectionPoints,
                onAdded = { item -> item.value.shape = this },
                onRemoved = { item -> item.value.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, controlPoints,
                onAdded = { item -> item.value.shape = this },
                onRemoved = { item -> item.value.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, scratches,
                onAdded = { item -> item.value.setContext(context) },
                onRemoved = { item -> item.value.setContext(constantsContext) }
        ))
    }

    override fun populateShape(newShape: Shape) {
        super.populateShape(newShape)

        if (newShape is RealShape) {
            geometries.forEach { geometryProp ->
                val geometry = geometryProp.value
                newShape.addGeometry(geometry.copy())
            }
            connectionPoints.forEach { connectionPointProp ->
                val connectionPoint = connectionPointProp.value
                newShape.addConnectionPoint(connectionPoint.copy())
            }
            controlPoints.forEach { controlPointProp ->
                val controlPoint = controlPointProp.value
                newShape.addControlPoint(controlPoint.copy())
            }
            scratches.forEach { scratchProp ->
                val scratch = scratchProp.value
                newShape.addScratch(scratch.copy())
            }
            newShape.lineWidth.formula = lineWidth.formula
            newShape.size.formula = size.formula
            newShape.lineColor.formula = lineColor.formula
            newShape.fillColor.formula = fillColor.formula
        }
    }

    override fun addMetaData(list: MutableList<MetaData>) {
        super.addMetaData(list)
        geometries.forEachIndexed { index, geometryProp -> geometryProp.value.addMetaData(list, index) }
        connectionPoints.forEachIndexed { index, connectionPointProp -> connectionPointProp.value.addMetaData(list, index) }
        controlPoints.forEachIndexed { index, controlPointProp -> controlPointProp.value.addMetaData(list, index) }
        scratches.forEachIndexed { index, scratchProp -> scratchProp.value.addMetaData(list, index) }
        list.add(MetaData("LineWidth", lineWidth))
        list.add(MetaData("Size", size))
        list.add(MetaData("LineColor", lineColor))
        list.add(MetaData("FillColor", fillColor))
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(GeometryProp(geometry))
    }

    fun addConnectionPoint(connectionPoint: ConnectionPoint) {
        connectionPoints.add(ConnectionPointProp(connectionPoint))
    }

    fun addControlPoint(controlPoint: ControlPoint) {
        controlPoints.add(ControlPointProp(controlPoint))
    }

    fun addScratch(scratch: Scratch) {
        scratches.add(ScratchProp(scratch))
    }

    fun findScratch(name: String): Scratch? {
        scratches.forEach { scratchProp ->
            if (scratchProp.value.name.value == name) {
                return scratchProp.value
            }
        }
        return null
    }
}
