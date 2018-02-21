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

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Vector2

class Vector2PropType private constructor() : PropType<Vector2>(Vector2::class) {

    override fun findField(prop: Prop<Vector2>, name: String): PropField<Vector2, *>? {
        return when (name) {
            "x" -> PropField<Vector2, Double>(prop) { prop.value.x }
            "y" -> PropField<Vector2, Double>(prop) { prop.value.y }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<Vector2>, name: String): PropMethod<Vector2, *>? {
        return when (name) {
            "length" -> PropMethod0(prop) { prop.value.length() }
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "angle" -> PropMethod0(prop) { prop.value.angle() }
            "rotate" -> PropMethod1(prop, Angle::class) { prop.value.rotate(it) }
            else -> null
        }
    }

    companion object {
        val instance = Vector2PropType()

        fun create(a: Prop<Double>, b: Prop<Double>): Prop<Vector2> {
            if (a.isConstant() && b.isConstant()) {
                return PropConstant(Vector2(a.value, b.value))
            } else {
                return PropCalculation2(a, b) { av, bv -> Vector2(av, bv) }
            }
        }
    }
}

class Vector2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Vector2>(expression, Vector2::class, context)
