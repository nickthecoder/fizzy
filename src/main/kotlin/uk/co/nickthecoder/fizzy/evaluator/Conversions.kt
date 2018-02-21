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
package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.*

/**
 * Identifiers that are placed AFTER the argument being manipulated, used to convert the value into one with units.
 * e.g. "5 cm" converts the Double 5 into a Dimension with units of centimeters.
 */
object Conversions {

    val conversions = mutableMapOf<String, (Prop<*>) -> Prop<*>>(
            "deg" to ::degConversion, // Double -> Angle (degrees)
            "rad" to ::radConversion, // Double -> Angle (radians)
            "mm" to ::mmConversion, // Double -> Dimension (millimeters)
            "cm" to ::cmConversion, // Double -> Dimension (centimeters)
            "m" to ::mConversion, // Double -> Dimension (meters)
            "km" to ::kmConversion // Double -> Dimension (kilometers)
    )

    fun find(str: String): ((Prop<*>) -> Prop<*>)? = conversions[str]
}
