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

/**
 * A unit-less 2 dimensional vector.
 * When working with points and lines etc, it is better to use [Dimension2] so that the lengths can have
 * arbitrary units (mm, cm, m, km etc).
 */
class Vector2(val x: Double, val y: Double) {

    operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)

    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)

    operator fun unaryMinus() = Vector2(-x, -y)

    operator fun times(scale: Double) = Vector2(x * scale, y * scale)

    operator fun times(other: Vector2) = Vector2(x * other.x, y * other.y)

    operator fun times(other: Dimension2) = Dimension2(other.x * x, other.y * y)

    operator fun div(scale: Double) = Vector2(x / scale, y / scale)

    operator fun div(other: Vector2) = Vector2(x / other.x, y / other.y)

    fun length() = Math.sqrt(x * x + y * y)

    /**
     * Create a Vector of length 1 pointing in the same direction as this.
     */
    fun normalise(): Vector2 {
        val l = length()
        return Vector2(x / l, y / l)
    }

    /**
     * The angle of the vector. 0° is along the x axis, and +ve values are anticlockwise if the y axis is pointing upwards.
     */
    fun angle(): Angle {
        return Angle.radians(Math.atan2(y, x))
    }

    /**
     * Rotates the vector about the origin. The rotation is anticlockwise if the y axis points upwards.
     */
    fun rotate(angle: Angle) = rotate(angle.radians)

    /**
     * Rotates the vector about the origin. The rotation is anticlockwise if the y axis points upwards.
     */
    fun rotate(radians: Double): Vector2 {
        val sin = Math.sin(radians)
        val cos = Math.cos(radians)
        return Vector2(cos * x - sin * y, sin * x + cos * y)
    }

    override fun toString() = "Vector2($x , $y)"

    companion object {
        val ZERO = Vector2(0.0, 0.0)
        val UNIT = Vector2(1.0, 1.0)
    }

}
