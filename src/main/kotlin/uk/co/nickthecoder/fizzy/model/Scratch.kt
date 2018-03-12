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

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners
import uk.co.nickthecoder.fizzy.util.toFormula

private fun createPropExpression(type: String): PropExpression<*> {
    return when (type) {
        "boolean" -> DoubleExpression(false.toFormula())
        "double" -> DoubleExpression(0.0.toFormula())
        "Dimension2" -> Dimension2Expression(Dimension2.ZERO_mm)
        else -> throw IllegalArgumentException("Scratch. Unknown type $type")
    }
}

class Scratch(name: String, var expression: PropExpression<*>, comment: String = "")
    : HasChangeListeners<Scratch>, PropListener, MetaDataAware {

    constructor(type: String) : this("", createPropExpression(type), "")

    override val changeListeners = ChangeListeners<Scratch>()

    val name = PropVariable(name)

    /**
     * Used only as documentation of the Master Shape, and is NOT available for use in formulas.
     * Therefore it is not a [Prop].
     */
    var comment = PropVariable(comment)

    var type = PropVariable(expression.klass.simpleName)

    private val typeListener = object : PropListener {
        override fun dirty(prop: Prop<*>) {
            val oldExpression = expression
            expression = createPropExpression(type.value)
            expression.formula = oldExpression.formula
            expression.propListeners.items.addAll(oldExpression.propListeners.items)
            oldExpression.propListeners.items.clear()
        }
    }

    init {
        type.propListeners.add(typeListener)
    }

    override fun metaData(): MetaData {
        val md = MetaData(null)
        addMetaData(md)
        return md
    }

    fun addMetaData(metaData: MetaData) {
        metaData.newCell("Type", type)
        metaData.newCell("Name", name)
        metaData.newCell("Comment", comment)
        metaData.newCell("Expression", expression)
    }

    fun setContext(context: EvaluationContext) {
        expression.context = context
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this)
    }

    fun copy(link: Boolean) = Scratch(name.value, expression.copy(link), comment.value)
}
