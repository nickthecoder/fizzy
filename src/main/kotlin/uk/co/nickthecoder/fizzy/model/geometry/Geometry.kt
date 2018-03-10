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
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.MetaData
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.BooleanExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners
import uk.co.nickthecoder.fizzy.util.toFormula

class Geometry

    : HasChangeListeners<Geometry>, PropListener {

    override val changeListeners = ChangeListeners<Geometry>()

    var shape: Shape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    fill.propListeners.remove(it)
                    stroke.propListeners.add(it)
                    connect.propListeners.add(it)
                }

                field = v

                parts.forEach { part ->
                    val context = v?.context ?: constantsContext
                    part.setContext(context)
                    fill.context = context
                    stroke.context = context
                    connect.context = context
                    if (v != null) {
                        fill.propListeners.add(v)
                        stroke.propListeners.add(v)
                        connect.propListeners.add(v)
                    }
                }
            }
        }

    var parts = MutableFList<GeometryPart>()

    val fill = BooleanExpression("false")
    val stroke = BooleanExpression("true")
    val connect = BooleanExpression("false")

    private val geometryPartsListener = ChangeAndCollectionListener(this, parts,
            onAdded = {
                part ->
                part.geometry = this
                part.setContext(shape?.context ?: constantsContext)
            },
            onRemoved = {
                part ->
                part.geometry = null
                part.setContext(constantsContext)
            }
    )


    /**
     * When a [GeometryPart] is added/removed, make this [Prop] dirty.
     * By doing so, if a calculation depends on this (i.e. it listens to me), then adding/removing parts will cause it
     * to be re-evaluated. This can then make the final calculation take its data from a different [GeometryPart], as
     * their indices have changed.
     */
    private val collectionListener = ChangeAndCollectionListener(this, parts,
            onAdded = { changeListeners.fireChanged(this) },
            onRemoved = { changeListeners.fireChanged(this) }
    )

    init {
        fill.propListeners.add(this)
        stroke.propListeners.add(this)
        connect.propListeners.add(this)
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this)
    }

    fun index(): Int {
        shape?.let {
            it.geometries.forEachIndexed { index, prop ->
                if (prop === this) {
                    return index
                }
            }
        }
        return -1
    }

    fun addMetaData(list: MutableList<MetaData>, sectionIndex: Int) {
        list.add(MetaData("Fill", fill, "Geometry", sectionIndex))
        list.add(MetaData("Stroke", stroke, "Geometry", sectionIndex))
        list.add(MetaData("Connect", connect, "Geometry", sectionIndex))
        parts.forEachIndexed { index, part -> part.addMetaData(list, sectionIndex, index) }
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
        shape?.let {
            return "connectAlong( Page.Shape${it.id}.Geometry${index() + 1}, ${along.toFormula()})"
        }
        return null
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

    fun copy(link: Boolean): Geometry {
        val newGeometry = Geometry()
        newGeometry.fill.copyFrom(fill, link)
        newGeometry.stroke.copyFrom(stroke, link)
        newGeometry.connect.copyFrom(connect, link)
        parts.forEach { part ->
            newGeometry.parts.add(part.copy(link))
        }
        return newGeometry
    }

}
