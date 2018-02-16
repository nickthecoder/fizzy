package uk.co.nickthecoder.fizzy.model

class Vector2(val x: Double, val y: Double) {

    operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)

    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)

    operator fun times(scale: Double) = Vector2(x * scale, y * scale)

    operator fun times(other: Vector2) = Vector2(x * x, y * y)


    operator fun div(scale: Double) = Vector2(x / scale, y / scale)

    operator fun div(other: Vector2) = Vector2(x / x, y / y)


    companion object {
        val ZERO = Vector2(0.0, 0.0)
        val UNIT = Vector2(1.0, 1.0)
    }

}
