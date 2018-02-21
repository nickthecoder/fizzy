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
            if (shape is Shape2d) {
                dc.translate(shape.position.value)
                dc.rotate(shape.rotation.value)
                dc.scale(shape.scale.value)
                dc.translate(-shape.localPosition.value.x, -shape.localPosition.value.y)
            }

            dc.lineWidth(shape.geometry.lineWidth.value)
            dc.beginPath()
            shape.geometry.parts.forEach { part ->
                when (part) {
                    is MoveTo -> dc.moveTo(part.point.value)
                    is LineTo -> dc.lineTo(part.point.value)
                }
            }
            dc.endPath()
        }
    }

}
