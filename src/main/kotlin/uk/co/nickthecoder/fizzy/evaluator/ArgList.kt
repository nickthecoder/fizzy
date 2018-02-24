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
package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.AbstractProp
import uk.co.nickthecoder.fizzy.prop.Prop

/**
 * Used to hold a list of arguments. The [ApplyOperator] "(", works on a SINGLE value, so if a function takes more than
 * one value, then the values are held in a single [ArgList].
 *
 * The "," operator converts a pair of non-ArgList value (such as Prop<Double> ), into an ArgList
 * The "," operator given an ArgList and an non-ArgList value adds the non-argList value to the list.
 *
 * In this way, when the ")" is found, there will be a single ArgList for the [ApplyOperator] to process.
 */
class ArgList : AbstractProp<MutableList<Prop<*>>>() {

    override val value = mutableListOf<Prop<*>>()

    override fun toString(): String {
        return value.map { it.value }.joinToString(prefix = "( ", separator = " , ", postfix = " )")
    }

}
