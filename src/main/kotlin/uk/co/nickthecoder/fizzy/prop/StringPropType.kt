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

class StringPropType private constructor()
    : PropType<String>(String::class) {

    override fun findField(prop: Prop<String>, name: String): Prop<*>? {
        return when (name) {
            "Length" -> SimplePropField("String.Length", prop) { prop.value.length.toDouble() }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<String>, name: String): PropMethod<String>? {
        return when (name) {
            "head" -> PropMethod1(prop, Double::class) { a ->
                val intA = a.toInt()
                if (prop.value.length <= intA) {
                    prop.value
                } else {
                    prop.value.substring(0, a.toInt())
                }
            }
            "tail" -> PropMethod1(prop, Double::class) { a ->
                val length = prop.value.length
                val intA = a.toInt()
                if (length <= intA) {
                    prop.value
                } else {
                    prop.value.substring(length - intA, length)
                }
            }
            "substring" -> PropMethod2(prop, Double::class, Double::class) { a, b ->
                var intA = a.toInt()
                val intB = b.toInt()
                val length = prop.value.length
                if (intA >= intB) {
                    ""
                } else {
                    if (intA < 0) {
                        intA = 0
                    }
                    if (intA >= length || intB < 0) {
                        ""
                    } else {
                        if (intA + intB >= length) {
                            prop.value.substring(intA)
                        } else {
                            prop.value.substring(intA, intB)
                        }
                    }
                }
            }
            else -> null
        }
    }

    companion object {
        val instance = StringPropType()
    }
}

class StringExpression
    : PropExpression<String> {

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, String::class, context)

    constructor(other: StringExpression) : super(other, String::class)

    override val defaultValue = ""

    override fun constant(value: String) {
        formula = value.toFormula()
    }

    override fun copy(link: Boolean) = if (link) StringExpression(this) else StringExpression(formula)

    override fun valueString() = "\"$value\""
}
