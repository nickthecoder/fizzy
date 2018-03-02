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
