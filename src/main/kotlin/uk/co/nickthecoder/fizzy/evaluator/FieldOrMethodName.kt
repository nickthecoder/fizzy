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
import uk.co.nickthecoder.fizzy.prop.PropField
import uk.co.nickthecoder.fizzy.prop.PropType

/**
 * When an identifier token (such as "foo") is found, if the topmost operator is a [DotOperator], then
 * the identifier token is converted to a [FieldOrMethodName] and pushed onto the [Evaluator.values] stack.
 *
 * If there is no "(", then applying the [DotOperator] creates a [PropField].
 * The correct [PropField] is found by finding the KClass of the value to the left of the ".". The KClass, and the
 * [FieldOrMethodName.value] is passed to [PropType.field].
 *
 * At the time of writing this methods haven't been implemented yet, so that process is not clear!
 *
 * [FieldOrMethodName]s are temporary values placed on the values stack, but do not form part of the final
 * structure from [Evaluator.parse].
 */
class FieldOrMethodName(name: String) : AbstractProp<String>() {

    override val value = name

    override fun toString(): String {
        return "FieldOrMethodName : $value"
    }
}
