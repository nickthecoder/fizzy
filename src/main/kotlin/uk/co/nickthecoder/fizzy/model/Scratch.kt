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

class Scratch(name: String, val expression: PropExpression<*>, comment: String = "")
    : HasChangeListeners<Scratch>, PropListener {

    override val changeListeners = ChangeListeners<Scratch>()

    val name = PropVariable<String>(name)

    /**
     * Used only as documentation of the Master Shape, and is NOT available for use in formulas.
     * Therefore it is not a [Prop].
     */
    var comment = comment

    fun addMetaData(metaData: MetaData, index: Int) {
        metaData.cells.add(MetaDataCell("Name", StringExpression(name.value.toFormula()), "Scratch", index))
        metaData.cells.add(MetaDataCell("Comment", StringExpression(comment.toFormula()), "Scratch", index))
        metaData.cells.add(MetaDataCell("Expression", expression, "Scratch", index))
    }

    fun setContext(context: EvaluationContext) {
        expression.context = context
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this)
    }

    fun copy(link: Boolean) = Scratch(name.value, expression.copy(link), comment)
}
