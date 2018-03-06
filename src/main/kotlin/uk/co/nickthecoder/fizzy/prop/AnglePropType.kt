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

class AnglePropType private constructor()
    : PropType<Angle>(Angle::class) {

    override fun findField(prop: Prop<Angle>, name: String): Prop<*>? {
        return when (name) {
            "Degrees" -> PropField(prop) { prop.value.degrees }
            "Radians" -> PropField(prop) { prop.value.radians }
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Angle>, name: String): PropMethod<Angle>? {
        return when (name) {
            "sin" -> PropMethod0(prop) { Math.sin(prop.value.radians) }
            "cos" -> PropMethod0(prop) { Math.cos(prop.value.radians) }
            "tan" -> PropMethod0(prop) { Math.tan(prop.value.radians) }

            "sinh" -> PropMethod0(prop) { Math.sinh(prop.value.radians) }
            "cosh" -> PropMethod0(prop) { Math.cosh(prop.value.radians) }
            "tanh" -> PropMethod0(prop) { Math.tanh(prop.value.radians) }
            else -> null
        }
    }

    companion object {
        val instance = AnglePropType()

        fun create(a: Prop<Double>, degrees: Boolean): Prop<Angle> {
            if (a.isConstant()) {
                if (degrees) {
                    return PropConstant(Angle.degrees(a.value))
                } else {
                    return PropConstant(Angle.radians(a.value))
                }
            } else {
                if (degrees) {
                    return PropCalculation1(a) { av -> Angle.degrees(av) }
                } else {
                    return PropCalculation1(a) { av -> Angle.radians(av) }
                }
            }
        }
    }
}


class AngleExpression
    : PropExpression<Angle> {

    constructor(formula: String, context: EvaluationContext = constantsContext) : super(formula, Angle::class, context)

    constructor(other: AngleExpression) : super(other.formula, Angle::class)

    override val defaultValue = Angle.ZERO

    override fun constant(value: Angle) {
        formula = value.toFormula()
    }

    override fun copy(link: Boolean) = if (link) AngleExpression(this) else AngleExpression(formula)

    override fun valueString() = value.toFormula()
}

fun degConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return AnglePropType.create(a as Prop<Double>, degrees = true)
    }
    return throwExpectedType("Double", a)
}

fun radConversion(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return AnglePropType.create(a as Prop<Double>, degrees = false)
    }
    return throwExpectedType("Double", a)
}
