package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.prop.PropExpression

class ChangeExpressions(expressions: List<Pair<PropExpression<*>, String>>)
    : Change {

    class State(val oldExpression: String, var newExpression: String)

    val states = mutableMapOf<PropExpression<*>, State>()

    init {
        expressions.forEach { (expression, newValue) ->
            states[expression] = State(expression.expression, newValue)
        }
    }

    override fun redo() {
        states.forEach { expression, state ->
            expression.expression = state.newExpression
        }
    }

    override fun undo() {
        states.forEach { expression, state ->
            expression.expression = state.oldExpression
        }
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is ChangeExpressions) {

            states.forEach { expression, myState ->
                val otherState = other.states[expression]
                if (otherState == null) {
                    other.states[expression] = State(expression.expression, myState.newExpression)
                } else {
                    otherState.newExpression = myState.newExpression
                }
            }

            return true
        }
        return false
    }
}
