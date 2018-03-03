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

class Scratch(name: String, val expression: PropExpression<*>) {

    val name = PropVariable<String>(name)

    /**
     * Used only as documentation of the Master Shape, and is NOT available for use in formulas.
     * Therefore it is not a [Prop].
     */
    var comment = ""

    fun addMetaData(list: MutableList<MetaData>, index: Int) {
        list.add(MetaData("Name", StringExpression(name.value), "Scratch", index))
        list.add(MetaData("Comment", StringExpression(comment), "Scratch", index))
        list.add(MetaData("Expression", expression, "Scratch", index))
    }

    fun setContext(context: EvaluationContext) {
        expression.context = context
    }

    fun copy() = Scratch(name.value, expression.copy())
}

class ScratchProp(scratch: Scratch)
    : PropValue<Scratch>(scratch),
        PropListener,
        HasChangeListeners<ScratchProp> {

    override val changeListeners = ChangeListeners<ScratchProp>()

    init {
        scratch.name.propListeners.add(this)
        scratch.expression.propListeners.add(this)
    }

    /**
     * Any changes to Scratch's data causes this [Prop]'s propListeners to be notified.
     * The [ScratchProp]'s constructor adds itself to the listeners of each of [Scratch]'s [Prop]s.
     */
    override fun dirty(prop: Prop<*>) {
        propListeners.fireDirty(this)
    }
}
