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
import uk.co.nickthecoder.fizzy.model.Vector2

class Vector2PropType private constructor() : PropType<Vector2>(Vector2::class) {

    override fun findField(prop: Prop<Vector2>, name: String): Prop<*>? {
        return when (name) {
            "X" -> PropField(prop) { prop.value.x }
            "Y" -> PropField(prop) { prop.value.y }
            "Angle" -> PropField(prop) { prop.value.angle() }
            "Length" -> PropField(prop) { prop.value.length() }
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Vector2>, name: String): PropMethod<Vector2>? {
        return when (name) {
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "rotate" -> PropMethod1(prop, Angle::class) { prop.value.rotate(it) }
            else -> null
        }
    }

    companion object {
        val instance = Vector2PropType()
    }
}

class Vector2Expression(expression: String, context: EvaluationContext = constantsContext)
    : PropExpression<Vector2>(expression, Vector2::class, context)