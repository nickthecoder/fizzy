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
package uk.co.nickthecoder.fizzy.util

import java.text.DecimalFormat

val terseDoubleFormat = DecimalFormat("0.####")

val formulaDoubleFormat = DecimalFormat("0.#########")

fun Double.terse() = terseDoubleFormat.format(this)

fun Double.toFormula() = if (this.isNaN()) "NaN" else formulaDoubleFormat.format(this)

fun Boolean.toFormula() = this.toString()

fun String.toFormula() = "\"${this.replace("\"", "\\\"")}\""

fun Double.ratio(other: Double): Double {
    return if (this == 0.0 && other == 0.0) 1.0 else this / other
}
