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
package uk.co.nickthecoder.fizzy.view

import javafx.application.Platform
import uk.co.nickthecoder.fizzy.model.*

class LayerView(val layer: Layer, val dc: DrawContext)
    : ChangeListener<Layer> {

    private var dirty = true
        set(v) {
            if (v != field) {
                field = v
                if (v) {
                    Platform.runLater { draw() }
                }
            }
        }


    override fun changed(item: Layer, changeType: ChangeType, obj: Any?) {
        dirty = true
    }

    fun draw() {
        dc.use {
            layer.children.forEach { shape ->
                drawShape(shape)
            }

            dirty = false
        }
    }

    fun drawShape(shape: Shape) {
        dc.use {

            if (shape is HasShapeTransform) {
                dc.translate(shape.transform.pin.value)
                dc.rotate(shape.transform.rotation.value)
                dc.scale(shape.transform.scale.value)
                dc.translate(-shape.transform.locPin.value.x, -shape.transform.locPin.value.y)
            }

            if (shape is RealShape) {

                shape.geometries.forEach { geometry ->
                    dc.beginPath()
                    geometry.parts.forEach { part ->
                        when (part) {
                            is MoveTo -> dc.moveTo(part.point.value)
                            is LineTo -> dc.lineTo(part.point.value)
                        }
                    }
                    dc.endPath()
                }
            }

            shape.children.forEach { child ->
                drawShape(child)
            }
        }
    }

}
