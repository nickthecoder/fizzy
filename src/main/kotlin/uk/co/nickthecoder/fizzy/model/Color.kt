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

import uk.co.nickthecoder.fizzy.util.toFormula

class Color(
        val red: Double,
        val green: Double,
        val blue: Double,
        val opacity: Double = 1.0)
    : Paint {

    init {
        if (red < 0.0 || green < 0.0 || blue < 0.0 || opacity < 0.0 || red > 1.0 || green > 1.0 || blue > 1.0 || opacity > 1.0) {
            throw IllegalArgumentException("Color channel values must be in the range 0..1")
        }
    }

    override fun isVisible() = opacity > 0.001

    fun isFullyOpaque() = opacity >= 1.0

    fun darker(scale: Double = 0.7): Color = Color(clamp(red * scale), clamp(green * scale), clamp(blue * scale), opacity)

    fun brighter(scale: Double = 1 / 0.7): Color = darker(scale)

    fun transparent(opacity: Double = 0.0) = Color(red, green, blue, opacity)


    override fun toFormula(): String {
        if (isFullyOpaque()) {
            return "RGB(${red.toFormula()},${green.toFormula()},${blue.toFormula()})"
        } else {
            return "RGBA(${red.toFormula()},${green.toFormula()},${blue.toFormula()},${opacity.toFormula()})"
        }
    }

    companion object {
        val WHITE = Color(1.0, 1.0, 1.0)
        val BLACK = Color(0.0, 0.0, 0.0)
        val TRANSPARENT = Color(0.0, 0.0, 0.0, 0.0)

        fun clamp(channel: Double): Double = Math.min(1.0, Math.max(0.0, channel))

        fun parseChannel(str: String): Double {
            val hex = str.toLong(16)
            return hex.toDouble() / 255.0
        }

        fun web(hashrgba: String): Color {
            if (hashrgba.startsWith("#")) {
                var opacity = 1.0
                if (hashrgba.length == 9) {
                    opacity = parseChannel(hashrgba.substring(7, 9))
                }
                return Color(
                        parseChannel(hashrgba.substring(1, 3)),
                        parseChannel(hashrgba.substring(3, 5)),
                        parseChannel(hashrgba.substring(5, 7)),
                        opacity
                )
            }
            throw IllegalArgumentException("Expected a color in the form #rrggbb or #rrggbaa")
        }
    }
}
