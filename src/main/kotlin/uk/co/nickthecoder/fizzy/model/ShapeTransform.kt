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

import uk.co.nickthecoder.fizzy.prop.*

/**
 * Holds the position of a shape within a parent (which is either a [ShapeGroup] or a [Page].
 *
 * Used by [Shape2d] and [ShapeGroup].
 */
class ShapeTransform(val shape: Shape)
    : MetaDataAware {

    /**
     * The position of this object relative to the parent (which is either a [Shape] or a [Page]).
     */
    val pin = Dimension2Expression("Dimension2(0mm, 0mm)", shape.context)

    /**
     * The local position of this object, which corresponds to the [pin] within the parent.
     * It is also used as the center of rotation.
     * (0,0) would be the top-left of the shape, and [Shape2d.size] would be the bottom right.
     * The default value is [Shape2d.size] / 2, which is the center of the shape.
     */
    val locPin = Dimension2Expression("Size / 2", shape.context)

    // Should we have a scale? A scale would scale the line widths, the fonts etc
    val scale = Vector2Expression("Vector2(1, 1)", shape.context)

    val rotation = AngleExpression("0 deg", shape.context)

    val flipX = BooleanExpression(false, shape.context)

    val flipY = BooleanExpression(false, shape.context)

    /**
     * A matrix which can transform a point from the coordinate system of the parent to local coordinates.
     * The inverse is [fromLocalToParent].
     * This is based on [pin], [locPin], [scale] and [rotation].
     * Whenever any of those become dirty, then this also becomes dirty.
     */
    val fromParentToLocal = object : PropCalculation<Matrix33>() {
        init {
            pin.propListeners.add(this)
            locPin.propListeners.add(this)
            scale.propListeners.add(this)
            rotation.propListeners.add(this)
            flipX.propListeners.add(this)
            flipY.propListeners.add(this)
        }

        override fun eval() =
                Matrix33.translate(locPin.value.x.inDefaultUnits, locPin.value.y.inDefaultUnits) *
                        Matrix33.scale(1.0 / scale.value.x, 1.0 / scale.value.y) *
                        Matrix33.rotate(-rotation.value) *
                        Matrix33.flip(flipX.value, flipY.value) *
                        Matrix33.translate(-pin.value.x.inDefaultUnits, -pin.value.y.inDefaultUnits)
    }

    /**
     * A matrix which can transform a point from a local coordinate into a coordinate in the parent's coordinate system.
     * The inverse is [fromParentToLocal].
     * This is based on [pin], [locPin], [scale] and [rotation].
     * Whenever any of those become dirty, then this also becomes dirty.
     */
    val fromLocalToParent = object : PropCalculation<Matrix33>() {
        init {
            pin.propListeners.add(this)
            locPin.propListeners.add(this)
            scale.propListeners.add(this)
            rotation.propListeners.add(this)
            flipX.propListeners.add(this)
            flipY.propListeners.add(this)
        }

        override fun eval() =
                Matrix33.translate(pin.value.x.inDefaultUnits, pin.value.y.inDefaultUnits) *
                        Matrix33.flip(flipX.value, flipY.value) *
                        Matrix33.rotate(rotation.value) *
                        Matrix33.scale(scale.value) *
                        Matrix33.translate(-locPin.value.x.inDefaultUnits, -locPin.value.y.inDefaultUnits)
    }


    /**
     * A matrix which can transform a local coordinate into a coordinate of the page.
     */
    val fromLocalToPage = object : PropCalculation<Matrix33>() {
        init {
            shape.parent.fromLocalToPage.propListeners.add(this)
            fromLocalToParent.propListeners.add(this)
        }

        override fun eval(): Matrix33 {
            return shape.parent.fromLocalToPage.value * fromLocalToParent.value
        }
    }
    /**
     * A matrix which can transform a local coordinate into a coordinate of the page.
     */
    val fromPageToLocal = object : PropCalculation<Matrix33>() {
        init {
            shape.parent.fromPageToLocal.propListeners.add(this)
            fromParentToLocal.propListeners.add(this)
        }

        override fun eval() =
                fromParentToLocal.value * shape.parent.fromPageToLocal.value
    }

    init {
        shape.listenTo(pin, locPin, scale, rotation, flipX, flipY)
    }

    override fun metaData(): MetaData {
        val metaData = MetaData(null)
        addMetaData(metaData)
        return metaData
    }

    fun addMetaData(metaData: MetaData) {
        val section = metaData.newSection("Transform")
        section.newCell("Pin", pin)
        section.newCell("LocPin", locPin)
        section.newCell("Scale", scale)
        section.newCell("Rotation", rotation)
        section.newCell("FlipX", flipX)
        section.newCell("FlipY", flipY)
    }

}
