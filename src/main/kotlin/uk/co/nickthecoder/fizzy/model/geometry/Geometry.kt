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

    : HasChangeListeners<Geometry>, PropListener {

    var parts = MutableFList<GeometryPart>()

    val fill = BooleanExpression("false")
    val stroke = BooleanExpression("false")
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
        fill.propListeners.add(this)
        stroke.propListeners.add(this)
        connect.propListeners.add(this)
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this, ChangeType.CHANGE, prop)
    }

    fun addMetaData(metaData: MetaData) {
        metaData.cells.add(MetaDataCell("Fill", fill, "Geometry"))
        metaData.cells.add(MetaDataCell("Stroke", stroke, "Geometry"))
        metaData.cells.add(MetaDataCell("Connect", connect, "Geometry"))
        parts.forEachIndexed { index, part -> part.addMetaData(metaData, index) }
    }

    fun isAt(localPoint: Dimension2, lineWidth: Dimension, minDistance: Dimension): Boolean {

        var prev: Dimension2? = null

        // Adapted from : https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
        var oddEven = false
        if (fill.value && parts.size > 2) {
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

        if (stroke.value) {
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
        newGeometry.fill.copyFrom(fill, link)
        newGeometry.stroke.copyFrom(stroke, link)
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
