package uk.co.nickthecoder.fizzy.view

import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2

interface DrawContext {

    fun save() {}

    fun restore() {}

    fun use(action: () -> Unit): DrawContext {
        save()
        action()
        restore()
        return this
    }

    fun translate(by: Dimension2)
    fun translate(x: Dimension, y: Dimension)

    fun rotate(by: Angle)

    fun scale(by: Vector2)


    fun lineWidth(width: Dimension)


    fun beginPath()

    fun endPath()

    fun moveTo(point: Dimension2)

    fun lineTo(point: Dimension2)

}

class CanvasContext(val canvas: Canvas)
    : DrawContext {

    val gc = canvas.graphicsContext2D

    override fun save() {
        gc.save()
    }

    override fun restore() {
        gc.restore()
    }


    override fun translate(by: Dimension2) {
        gc.translate(by.x.inDefaultUnits, by.y.inDefaultUnits)
    }

    override fun translate(x: Dimension, y: Dimension) {
        gc.translate(x.inDefaultUnits, y.inDefaultUnits)
    }

    override fun rotate(by: Angle) {
        gc.rotate(by.degrees)
    }

    override fun scale(by: Vector2) {
        gc.scale(by.x, by.y)
    }


    override fun lineWidth(width: Dimension) {
        gc.lineWidth = width.inDefaultUnits
    }


    override fun beginPath() {
        gc.beginPath()
    }

    override fun endPath() {
        gc.stroke()
    }

    override fun moveTo(point: Dimension2) {
        gc.moveTo(point.x.inDefaultUnits, point.y.inDefaultUnits)
    }

    override fun lineTo(point: Dimension2) {
        gc.lineTo(point.x.inDefaultUnits, point.y.inDefaultUnits)
    }
}
