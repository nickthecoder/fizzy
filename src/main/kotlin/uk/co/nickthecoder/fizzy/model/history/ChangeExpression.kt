package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.prop.PropExpression

class ChangeExpression(val expression: PropExpression<*>, var newExpression: String)
    : Change {

    val old = expression.expression

    override fun redo() {
        expression.expression = newExpression
    }

    override fun undo() {
        expression.expression = old
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is ChangeExpression && other.expression === expression) {
            other.newExpression = newExpression
            return true
        }
        return false
    }
}
