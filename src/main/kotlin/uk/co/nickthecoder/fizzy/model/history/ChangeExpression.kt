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
package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.prop.PropExpression

class ChangeExpression(val expression: PropExpression<*>, var newFormula: String)
    : Change {

    val old = expression.formula

    override fun redo() {
        expression.formula = newFormula
    }

    override fun undo() {
        expression.formula = old
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is ChangeExpression && other.expression === expression) {
            other.newFormula = newFormula
            return true
        }
        return false
    }
}
