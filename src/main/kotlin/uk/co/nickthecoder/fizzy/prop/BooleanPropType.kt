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
package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.util.toFormula

class BooleanPropType private constructor()
    : PropType<Boolean>(Boolean::class.java) {

    companion object {
        val instance = BooleanPropType()
    }
}

class BooleanExpression
    : PropExpression<Boolean> {

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, Boolean::class.java, context)

    constructor(other: BooleanExpression) : super(other)

    override val defaultValue = false

    override fun copy(link: Boolean) = if (link) BooleanExpression(formula) else BooleanExpression(formula)

    override fun valueString() = value.toString()
}
