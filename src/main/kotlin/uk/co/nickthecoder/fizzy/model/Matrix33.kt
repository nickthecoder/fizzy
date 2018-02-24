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

class Matrix33(
        val x1y1: Double = 1.0,
        val x2y1: Double = 0.0,
        val x3y1: Double = 0.0,
        val x1y2: Double = 0.0,
        val x2y2: Double = 1.0,
        val x3y2: Double = 0.0,
        val x1y3: Double = 0.0,
        val x2y3: Double = 0.0,
        val x3y3: Double = 1.0
) {

    /**
     * This performs a 3x3 matrix times by a 1x3 matrix, adding an extra 1.0 for the z value of the 1x3 matrix.
     */
    fun mul(x: Double, y: Double): Vector2 {
        return Vector2(
                x1y1 * x + x2y1 * y + x3y1,
                x1y2 * x + x2y2 * y + x3y2
        )
    }

    operator fun times(v2: Vector2) = mul(v2.x, v2.y)

    operator fun times(o: Matrix33): Matrix33 {
        return Matrix33(
                x1y1 * o.x1y1 + x2y1 * o.x1y2 + x3y1 * o.x1y3,
                x1y1 * o.x2y1 + x2y1 * o.x2y2 + x3y1 * o.x2y3,
                x1y1 * o.x3y1 + x2y1 * o.x3y2 + x3y1 * o.x3y3,

                x1y2 * o.x1y1 + x2y2 * o.x1y2 + x3y2 * o.x1y3,
                x1y2 * o.x2y1 + x2y2 * o.x2y2 + x3y2 * o.x2y3,
                x1y2 * o.x3y1 + x2y2 * o.x3y2 + x3y2 * o.x3y3,

                x1y3 * o.x1y1 + x2y3 * o.x1y2 + x3y3 * o.x1y3,
                x1y3 * o.x2y1 + x2y3 * o.x2y2 + x3y3 * o.x2y3,
                x1y3 * o.x3y1 + x2y3 * o.x3y2 + x3y3 * o.x3y3
        )
    }

    companion object {
        fun scale(xScale: Double, yScale: Double) = Matrix33(
                xScale, 0.0, 0.0,
                0.0, yScale, 0.0,
                0.0, 0.0, 1.0)

        fun translate(dx: Double, dy: Double) = Matrix33(
                1.0, 0.0, dx,
                0.0, 1.0, dy,
                0.0, 0.0, 1.0)

        fun rotate(angle: Angle): Matrix33 {
            val sin = Math.sin(angle.radians)
            val cos = Math.cos(angle.radians)

            return Matrix33(
                    cos, -sin, 0.0,
                    sin, cos, 0.0,
                    1.0, 1.0, 1.0
            )
        }

    }

    override fun toString() = "\n" +
            "| $x1y1 , $x2y1 , $x3y1 |\n" +
            "| $x1y2 , $x2y2 , $x3y2 |\n" +
            "| $x1y3 , $x2y3 , $x3y3 |\n"

}
