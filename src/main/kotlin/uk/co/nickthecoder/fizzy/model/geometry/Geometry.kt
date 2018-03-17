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
package uk.co.nickthecoder.fizzy.model.geometry

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.BooleanExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.*

class Geometry(val shape: Shape)

    : HasChangeListeners<Geometry>, PropListener, MetaDataAware {

    var parts = MutableFList<GeometryPart>()

    val connect = BooleanExpression("false")

    override val changeListeners = ChangeListeners<Geometry>()

    private val geometryPartsListener = ChangeAndCollectionListener(this, parts,
            onAdded = { part ->
                part.geometry = this
                part.setContext(shape.context)
            },
            onRemoved = { part ->
                part.geometry = null
                part.setContext(constantsContext)
            }
    )

    init {
        connect.propListeners.add(this)
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this, ChangeType.CHANGE, prop)
    }

    override fun createRow(type: String?): Pair<MetaDataAware, MetaData> {
        return when (type) {
            "MoveTo" -> {
                val rowObject = MoveTo()
                parts.add(rowObject)
                Pair(rowObject, rowObject.metaData())
            }
            "LineTo" -> {
                val rowObject = LineTo()
                parts.add(rowObject)
                Pair(rowObject, rowObject.metaData())
            }
            "BezierCurveTo" -> {
                val rowObject = BezierCurveTo()
                parts.add(rowObject)
                Pair(rowObject, rowObject.metaData())
            }
            else -> throw IllegalStateException("Geometry has no rows of type $type")
        }
    }


    override fun metaData(): MetaData {
        val md = MetaData(null)
        addMetaData(md)
        return md
    }

    fun addMetaData(metaData: MetaData) {
        metaData.newCell("Connect", connect)
        parts.forEach { part ->
            val row = metaData.newRow(part.javaClass.simpleName)
            part.addMetaData(row)
        }
        metaData.rowFactories.add(RowFactory("MoveTo") { parts.add(MoveTo()) })
        metaData.rowFactories.add(RowFactory("LineTo") { parts.add(LineTo()) })
        metaData.rowFactories.add(RowFactory("BezierCurveTo") { parts.add(BezierCurveTo()) })
    }

    fun isAt(localPoint: Dimension2, lineWidth: Dimension, minDistance: Dimension): Boolean {

        var prev: Dimension2? = null

        // Adapted from : https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
        var oddEven = false
        if (shape.fillColor.value.isVisible() && parts.size > 2) {
            parts.forEach { part ->
                prev?.let {
                    if (part.isCrossing(localPoint, it)) {
                        oddEven = !oddEven
                    }
                }
                prev = part.point.value
            }
            // If the Geometry is not closed. i.e. if the first point isn't the same as the last point,
            // then add an extra line to close the shape.
            val first = parts[0].point.value
            val last = parts[parts.size - 1].point.value
            if (first != last) {
                if (first != last && GeometryPart.isCrossing(localPoint, last, first)) {
                    oddEven = !oddEven
                }
            }
            if (oddEven) {
                return true
            }
        }

        if (shape.strokeColor.value.isVisible()) {
            prev = null
            parts.forEach { part ->
                prev?.let {
                    if (part.isAlong(shape, localPoint, it, lineWidth, minDistance)) {
                        return true
                    }
                }
                prev = part.point.value
            }
        }

        return false
    }

    fun connectAlongFormula(along: Double): String? {
        return "connectAlong( Page.Shape${shape.id}.Geometry, ${along.toFormula()})"
    }

    /**
     * Find the point part way along this geometry. 0 will be the start of the geometry,
     * 1 will be the end.
     */
    fun pointAlong(along: Double): Dimension2 {
        val nonMoveCount = parts.count { it !is MoveTo }

        // Clip to 0..1
        val alongClipped = if (along < 0) {
            0.0
        } else if (along > 1.0) {
            1.0
        } else {
            along
        }

        var partIndex = Math.floor(nonMoveCount * alongClipped).toInt()
        val partAlong = (alongClipped - partIndex.toDouble() / nonMoveCount) * nonMoveCount

        var prev: Dimension2? = null
        for (part in parts) {
            if (part !is MoveTo) {
                if (partIndex == 0) {
                    if (prev != null) {
                        return part.pointAlong(prev, partAlong)
                    }
                    break
                }
                partIndex--
            }
            prev = part.point.value
        }
        return Dimension2.ZERO_mm
    }

    fun copyInto(newShape: Shape, link: Boolean) {
        val newGeometry = newShape.geometry
        newGeometry.connect.copyFrom(connect, link)
        parts.forEach { part ->
            newGeometry.parts.add(part.copy(link))
        }
    }

    companion object {
        private val noDocument = Document()
        private val noPage = Page(noDocument)
        private val noShape = Shape2d.create(noPage)
        val NO_GEOMETRY = noShape.geometry
    }

}
