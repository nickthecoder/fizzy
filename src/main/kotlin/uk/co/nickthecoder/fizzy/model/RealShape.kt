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
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener


/**
 * The basis for Shape1d and Shape2d, i.e. the type of Shapes which have Geometries, ConnectionPoints etc.
 */
abstract class RealShape(parent: ShapeParent)
    : Shape(parent) {

    val geometries = MutableFList<Geometry>()

    val connectionPoints = MutableFList<ConnectionPoint>()

    val controlPoints = MutableFList<ControlPoint>()

    val scratches = ScratchList()

    abstract val size: Dimension2Expression

    val lineWidth = DimensionExpression("2mm")

    val strokeCap = StrokeCapExpression(StrokeCap.ROUND.toFormula())

    val strokeJoin = StrokeJoinExpression(StrokeJoin.ROUND.toFormula())

    val strokeColor = PaintExpression("BLACK")

    val fillColor = PaintExpression("WHITE")

    override fun isAt(point: Dimension2, minDistance: Dimension): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        geometries.forEach { geo ->
            if (geo.isAt(localPoint, lineWidth.value, minDistance)) {
                return true
            }
        }

        return super.isAt(point, minDistance)
    }

    override fun postInit() {
        listenTo(lineWidth)
        listenTo(strokeCap)
        listenTo(strokeJoin)
        listenTo(strokeColor)
        listenTo(fillColor)
        super.postInit()
        // Automatically tell the child of the parent when it is added to the list (and set to null when removed)
        // Also bubble change events up the hierarchy.
        collectionListeners.add(ChangeAndCollectionListener(this, geometries,
                onAdded = { geometry -> geometry.shape = this },
                onRemoved = { geometry -> geometry.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, connectionPoints,
                onAdded = { item -> item.shape = this },
                onRemoved = { item -> item.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, controlPoints,
                onAdded = { item -> item.shape = this },
                onRemoved = { item -> item.shape = null }
        ))
        collectionListeners.add(ChangeAndCollectionListener(this, scratches,
                onAdded = { item -> item.value.setContext(context) },
                onRemoved = { item -> item.value.setContext(constantsContext) }
        ))
    }

    override fun populateShape(newShape: Shape, link: Boolean) {
        super.populateShape(newShape, link)

        if (newShape is RealShape) {
            geometries.forEach { geometry ->
                newShape.addGeometry(geometry.copy(link))
            }
            connectionPoints.forEach { connectionPoint ->
                newShape.addConnectionPoint(connectionPoint.copy(link))
            }
            controlPoints.forEach { controlPoint ->
                newShape.addControlPoint(controlPoint.copy(link))
            }
            scratches.forEach { scratchProp ->
                val scratch = scratchProp.value
                newShape.addScratch(scratch.copy(link))
            }
            newShape.lineWidth.copyFrom(lineWidth, link)
            newShape.size.copyFrom(size, link)
            newShape.strokeColor.copyFrom(strokeColor, link)
            newShape.fillColor.copyFrom(fillColor, link)
            newShape.strokeCap.copyFrom(strokeCap, link)
            newShape.strokeJoin.copyFrom(strokeJoin, link)
        }
    }

    override fun addMetaData(list: MutableList<MetaData>) {
        super.addMetaData(list)
        geometries.forEachIndexed { index, geometryProp -> geometryProp.addMetaData(list, index) }
        connectionPoints.forEachIndexed { index, connectionPointProp -> connectionPointProp.addMetaData(list, index) }
        controlPoints.forEachIndexed { index, controlPointProp -> controlPointProp.addMetaData(list, index) }
        scratches.forEachIndexed { index, scratchProp -> scratchProp.value.addMetaData(list, index) }
        list.add(MetaData("LineWidth", lineWidth))
        list.add(MetaData("Size", size))
        list.add(MetaData("LineColor", strokeColor))
        list.add(MetaData("FillColor", fillColor))
        list.add(MetaData("StrokeCap", strokeCap))
        list.add(MetaData("StrokeJoin", strokeJoin))
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(geometry)
    }

    fun addConnectionPoint(connectionPoint: ConnectionPoint) {
        connectionPoints.add(connectionPoint)
    }

    fun addControlPoint(controlPoint: ControlPoint) {
        controlPoints.add(controlPoint)
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
