package uk.co.nickthecoder.fizzy.view

import uk.co.nickthecoder.fizzy.model.Vector2

class MockContext : AbsoluteContext() {

    val buffer = StringBuffer()

    override fun absoluteMoveTo(point: Vector2) {
        buffer.append("M${point.x},${point.y}\n")
    }

    override fun absoluteLineTo(point: Vector2) {
        buffer.append("L${point.x},${point.y}\n")
    }


    override fun beginPath() {
        println("Transform : ${state.transformation}")
    }

    override fun endPath() {
        println()
    }

    fun toList() = buffer.toString().split("\n")
}
