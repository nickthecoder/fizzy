package uk.co.nickthecoder.fizzy.model

class Dimension2(val x: Dimension, val y: Dimension) {

    operator fun plus(other: Dimension2): Dimension2 = Dimension2(x + other.x, y + other.y)

    operator fun minus(other: Dimension2) = Dimension2(x - other.x, y - other.y)

    operator fun times(scale: Double) = Dimension2(x * scale, y * scale)

    operator fun times(other: Dimension2) = Dimension2(x * other.x, y * other.y)

    operator fun times(other: Vector2) = Dimension2(x * other.x, y * other.y)

    operator fun div(scale: Double) = Dimension2(x / scale, y / scale)

    operator fun div(other: Dimension2) = Dimension2(x / other.x, y / other.y)

    operator fun div(other: Vector2) = Dimension2(x / other.x, y / other.y)

    /**
     * Finds the length of the 2D Dimension. It is assumed that x and y are both power 1, otherwise the result will make no sense.
     */
    fun length(): Dimension {
        return (x * x + y * y).sqrt()
    }

    /**
     * Returns a Vector of length 1, in the same direction as this 2D Dimension.
     * If you need a Dimension2 of unit length, then simply multiply this result by a Dimension of 1 (in whichever units you wish).
     *
     * Note, this assumes that x and y both have the same "power", as it makes so sense to normalise (meters,square meters) for example.
     */
    fun normalise(): Vector2 {
        val l = length().inDefaultUnits
        return Vector2(x.inDefaultUnits / l, y.inDefaultUnits / l)
    }

    override fun toString() = "Dimension2($x , $y)"

    companion object {
        val ZERO_mm = Dimension2(Dimension.ZERO_mm, Dimension.ZERO_mm)
    }

}
