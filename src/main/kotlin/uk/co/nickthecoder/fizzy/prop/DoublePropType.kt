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

class DoublePropType private constructor()
    : PropType<Double>(Double::class.java) {

    companion object {
        val instance = DoublePropType()
    }
}

class DoubleExpression
    : PropExpression<Double> {

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, Double::class.java, context)

    constructor(other: DoubleExpression) : super(other, Double::class.java)

    override val defaultValue: Double = java.lang.Double.parseDouble(".0")

    override fun constant(value: Double) {
        formula = value.toDouble().toFormula()
    }

    override fun copy(link: Boolean) = if (link) DoubleExpression(this) else DoubleExpression(formula, constantsContext)

    override fun valueString() = value.toDouble().toFormula()
}
