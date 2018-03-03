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

class ChangeExpressions(expressions: List<Pair<PropExpression<*>, String>>)
    : Change {

    class State(val oldFormula: String, var newFormula: String)

    val states = mutableMapOf<PropExpression<*>, State>()

    init {
        expressions.forEach { (expression, newValue) ->
            states[expression] = State(expression.formula, newValue)
        }
    }

    override fun redo() {
        states.forEach { expression, state ->
            expression.formula = state.newFormula
        }
    }

    override fun undo() {
        states.forEach { expression, state ->
            expression.formula = state.oldFormula
        }
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is ChangeExpressions) {

            states.forEach { expression, myState ->
                val otherState = other.states[expression]
                if (otherState == null) {
                    other.states[expression] = State(expression.formula, myState.newFormula)
                } else {
                    otherState.newFormula = myState.newFormula
                }
            }

            return true
        }
        return false
    }
}
