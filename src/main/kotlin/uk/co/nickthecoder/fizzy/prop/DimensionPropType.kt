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
import uk.co.nickthecoder.fizzy.model.Dimension

class DimensionPropType private constructor()
    : PropType<Dimension>(Dimension::class) {

    override fun findField(prop: Prop<Dimension>, name: String): Prop<*>? {
        return when (name) {
            "mm" -> SimplePropField("Dimension.mm", prop) { it.value.mm }
            "cm" -> SimplePropField("Dimension.cm", prop) { it.value.cm }
            "m" -> SimplePropField("Dimension.m", prop) { it.value.m }
            "km" -> SimplePropField("Dimension.km", prop) { it.value.km }
            else -> return super.findField(prop, name)
        }
    }

    companion object {
        val instance = DimensionPropType()

        init {
            PropType.put(instance)
        }

        fun create(a: Prop<Double>, units: Dimension.Units, power: Double = 1.0): Prop<Dimension> {
            if (a.isConstant()) {
                return PropConstant(Dimension(a.value, units, power))
            } else {
                return PropCalculation1(a) { av -> Dimension(av, units, power) }
            }
        }
    }
}

class DimensionExpression
    : PropExpression<Dimension> {

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, Dimension::class, context)

    constructor(other: DimensionExpression) : super(other, Dimension::class)

    override val defaultValue = Dimension.ZERO_mm

    override fun constant(value: Dimension) {
        formula = value.toFormula()
    }

    override fun copy(link: Boolean) = if (link) DimensionExpression(formula) else DimensionExpression(formula)

    override fun valueString() = value.toFormula()
}

fun dimensionConversion(a: Prop<*>, units: Dimension.Units, power: Double = 1.0): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return DimensionPropType.create(a as Prop<Double>, units, power)
    }
    return throwExpectedType("Double", a)
}

fun mmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.mm)
fun cmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.cm)
fun mConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.m)
fun kmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.km)
