package uk.co.nickthecoder.fizzy.model

class Dimension2(val x: Dimension, val y: Dimension) {

    operator fun plus(other: Dimension2): Dimension2 = Dimension2(x + other.x, y + other.y)

    operator fun minus(other: Dimension2) = Dimension2(x - other.x, y - other.y)

    operator fun times(scale: Double) = Dimension2(x * scale, y * scale)

    operator fun times(other: Dimension2) = Dimension2(x * other.x, y * other.y)

    operator fun div(scale: Double) = Dimension2(x / scale, y / scale)

    operator fun div(other: Dimension2) = Dimension2(x / other.x, y / other.y)


    companion object {
        val ZERO = Dimension2(Dimension.ZERO_mm, Dimension.ZERO_mm)
    }

}
