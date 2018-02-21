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

class Angle private constructor(val radians: Double) {

    val degrees: Double
        get() = Math.toDegrees(radians)

    operator fun plus(other: Angle) = Angle.radians(radians + other.radians)

    operator fun minus(other: Angle) = Angle.radians(radians - other.radians)

    operator fun unaryMinus() = Angle.radians(-radians)

    operator fun times(other: Double) = Angle.radians(radians * other)

    operator fun div(other: Double) = Angle.radians(radians / other)

    operator fun div(other: Angle) = radians / other.radians

    fun unitVector(): Vector2 = Vector2(1.0, 0.0).rotate(this)

    companion object {

        fun degrees(d: Double) = Angle(Math.toRadians(d))

        fun radians(r: Double) = Angle(r)

        val ZERO = Angle(0.0)
        val PI = Angle(Math.PI)
        val TAU = Angle(Math.PI * 2.0)
    }

    override fun equals(other: Any?): Boolean {
        return other is Angle && other.radians == this.radians
    }
}
