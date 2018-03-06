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
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension2

class Dimension2PropType private constructor()
    : PropType<Dimension2>(Dimension2::class) {

    override fun findField(prop: Prop<Dimension2>, name: String): Prop<*>? {

        return when (name) {
            "X" -> PropField(prop) { prop.value.x }
            "Y" -> PropField(prop) { prop.value.y }
            "Length" -> PropField(prop) { prop.value.length() }
            "Angle" -> PropField(prop) { prop.value.angle() }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Dimension2>, name: String): PropMethod<Dimension2>? {
        return when (name) {
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "rotate" -> PropMethod1(prop, Angle::class) { prop.value.rotate(it) }
            else -> null
        }
    }

    companion object {
        val instance = Dimension2PropType()
    }
}

class Dimension2Expression
    : PropExpression<Dimension2> {

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, Dimension2::class, context)

    constructor(pointValue: Dimension2, context: EvaluationContext = constantsContext) : this(pointValue.toFormula(), context)

    constructor(other: Dimension2Expression) : super(other, Dimension2::class)

    override val defaultValue = Dimension2.ZERO_mm

    override fun constant(value: Dimension2) {
        formula = value.toFormula()
    }

    override fun copy(link: Boolean) = if (link) Dimension2Expression(this) else Dimension2Expression(formula)

    override fun valueString() = value.toFormula()
}
