package uk.co.nickthecoder.fizzy.model

/**
 * A unit-less 2 dimensional vector.
 * When working with points and lines etc, it is better to use [Dimension2] so that the lengths can have
 * arbitrary units (mm, cm, m, km etc).
 */
class Vector2(val x: Double, val y: Double) {

    operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)

    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)

    operator fun times(scale: Double) = Vector2(x * scale, y * scale)

    operator fun times(other: Vector2) = Vector2(x * other.x, y * other.y)

    operator fun div(scale: Double) = Vector2(x / scale, y / scale)

    operator fun div(other: Vector2) = Vector2(x / other.x, y / other.y)


    companion object {
        val ZERO = Vector2(0.0, 0.0)
        val UNIT = Vector2(1.0, 1.0)
    }

}
