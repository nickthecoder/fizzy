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
    : PropType<Dimension2>(Dimension2::class.java) {

    override fun findField(prop: Prop<Dimension2>, name: String): Prop<*>? {

        return when (name) {
            "X" -> SimplePropField("Dimension2.X", prop) { it.value.x }
            "Y" -> SimplePropField("Dimension2.Y", prop) { it.value.y }
            "Length" -> SimplePropField("Dimension2.Length", prop) { it.value.length() }
            "Angle" -> SimplePropField("Dimension2.Angle", prop) { it.value.angle() }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Dimension2>, name: String): PropMethod<Dimension2>? {
        return when (name) {
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "rotate" -> PropMethod1(prop, Angle::class.java) { prop.value.rotate(it) }
            else -> null
        }
    }

    companion object {
        val instance = Dimension2PropType()
    }
}

class Dimension2Expression
    : PropExpression<Dimension2> {

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, Dimension2::class.java, context)

    constructor(pointValue: Dimension2, context: EvaluationContext = constantsContext) : this(pointValue.toFormula(), context)

    constructor(other: Dimension2Expression) : super(other)

    override val defaultValue = Dimension2.ZERO_mm


    override fun copy(link: Boolean) = if (link) Dimension2Expression(this) else Dimension2Expression(formula)

    override fun valueString() = value.toFormula()
}
