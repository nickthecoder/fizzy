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
