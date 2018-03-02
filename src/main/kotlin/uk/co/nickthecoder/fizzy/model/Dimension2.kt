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

class Dimension2(val x: Dimension, val y: Dimension) {

    operator fun plus(other: Dimension2): Dimension2 = Dimension2(x + other.x, y + other.y)

    operator fun minus(other: Dimension2) = Dimension2(x - other.x, y - other.y)

    operator fun unaryMinus() = Dimension2(-x, -y)

    operator fun times(scale: Double) = Dimension2(x * scale, y * scale)

    operator fun times(other: Dimension2) = Dimension2(x * other.x, y * other.y)

    operator fun times(other: Vector2) = Dimension2(x * other.x, y * other.y)

    operator fun div(scale: Double) = Dimension2(x / scale, y / scale)

    operator fun div(other: Dimension2) = Dimension2(x / other.x, y / other.y)

    operator fun div(other: Dimension) = Dimension2(x / other, y / other)

    operator fun div(other: Vector2) = Dimension2(x / other.x, y / other.y)

    /**
     * Returns the dot product of the two Dimension2s in default units.
     */
    fun dot(other: Dimension2) =
            x.inDefaultUnits * other.x.inDefaultUnits + y.inDefaultUnits * other.y.inDefaultUnits

    fun ratio(other: Dimension2): Vector2 {
        assert(x.power == other.x.power)
        assert(y.power == other.y.power)

        return Vector2(
                x.inDefaultUnits / other.x.inDefaultUnits,
                y.inDefaultUnits / other.y.inDefaultUnits
        )
    }

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

    fun aspectRatio() = x.inDefaultUnits / y.inDefaultUnits

    /**
     * The angle of the vector. 0° is along the x axis, and +ve values are anticlockwise if the y axis is pointing upwards.
     *
     * It assumes that the power for x and y is 1. The result makes no sense for other powers, as it is only sensible for lengths.
     */
    fun angle(): Angle = Angle.radians(Math.atan2(y.inDefaultUnits, x.inDefaultUnits))

    /**
     * Rotates anticlockwise about the origin.
     *
     * The units of both x and y will be in the units of x. i.e. if x is in cm and y is in m,
     * then the result will be in cm for both x and y
     *
     * It assumes that the power for x and y is 1. The result makes no sense for other powers, as it is only sensible for lengths.
     */
    fun rotate(angle: Angle) = rotate(angle.radians)

    /**
     * Rotates anticlockwise about the origin.
     *
     * The units of both x and y will be in the units of x. i.e. if x is in cm and y is in m,
     * then the result will be in cm for both x and y
     *
     * It assumes that the power for x and y is 1. The result makes no sense for other powers, as it is only sensible for lengths.
     */
    fun rotate(radians: Double): Dimension2 {
        val sin = Math.sin(radians)
        val cos = Math.cos(radians)
        return Dimension2(x * cos - y * sin, x * sin + y * cos)
    }

    override fun hashCode(): Int {
        return x.hashCode() + 17 * y.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Dimension2) {
            return x == other.x && y == other.y
        }
        return false
    }

    fun toExpression() = "Dimension2(${x.toExpression()},${y.toExpression()})"

    override fun toString() = "Dimension2($x , $y)"

    companion object {
        val ZERO_mm = Dimension2(Dimension.ZERO_mm, Dimension.ZERO_mm)
    }

}
