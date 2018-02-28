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
import uk.co.nickthecoder.fizzy.model.geometry.Geometry
import uk.co.nickthecoder.fizzy.model.geometry.GeometryProp
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener


/**
 * The basis for Shape1d and Shape2d, i.e. the type of Shapes which have Geometries, ConnectionPoints etc.
 */
abstract class RealShape(parent: ShapeParent)
    : Shape(parent) {

    val geometries = MutableFList<GeometryProp>()

    val connectionPoints = MutableFList<ConnectionPointProp>()

    val lineWidth = DimensionExpression("2mm")

    override fun isAt(point: Dimension2): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        geometries.forEach { geo ->
            if (geo.value.isAt(localPoint, lineWidth.value)) {
                return true
            }
        }

        return super.isAt(point)
    }

    override fun postInit() {
        listenTo(lineWidth)
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
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(GeometryProp(geometry))
    }

    fun addConnectionPoint(connectionPoint: ConnectionPoint) {
        connectionPoints.add(ConnectionPointProp(connectionPoint))
    }
}
