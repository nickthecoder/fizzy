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
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2

class Dimension2PropType private constructor()
    : PropType<Dimension2>(Dimension2::class) {

    override fun findField(prop: Prop<Dimension2>, name: String): Prop<*>? {

        return when (name) {
            "x" -> PropField<Dimension2, Dimension>(prop) { prop.value.x }
            "y" -> PropField<Dimension2, Dimension>(prop) { prop.value.y }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Dimension2>, name: String): PropMethod<Dimension2, *>? {
        return when (name) {
            "length" -> PropMethod0(prop) { prop.value.length() }
            "normalise" -> PropMethod0(prop) { prop.value.normalise() }
            "ratio" -> PropMethod1(prop, Dimension2::class) { prop.value.ratio(it) }
            "angle" -> PropMethod0(prop) { prop.value.angle() }
            "rotate" -> PropMethod1(prop, Angle::class) { prop.value.rotate(it) }
            else -> null
        }
    }

    companion object {
        val instance = Dimension2PropType()
    }
}

class Dimension2Expression(expression: String, context: Context = constantsContext)
    : PropExpression<Dimension2>(expression, Dimension2::class, context)
