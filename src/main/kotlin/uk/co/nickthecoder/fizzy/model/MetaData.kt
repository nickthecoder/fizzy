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

import uk.co.nickthecoder.fizzy.prop.PropExpression

data class MetaData(

        val cellName: String,
        val cellExpression: PropExpression<*>,
        val sectionName: String? = null,
        val sectionIndex: Int? = null,
        val rowIndex: Int? = null
) {

    fun accessString(): String {
        val result = StringBuffer()
        sectionName?.let { result.append(sectionName) }
        sectionIndex?.let { result.append("$it") }
        sectionName?.let { result.append(".") }
        result.append(cellName)
        return result.toString()
    }

    override fun toString(): String = "${accessString()} : ${cellExpression.formula} = ${cellExpression.valueString()}"

}