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
package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.model.Parent
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop

class FindShape(prop: Prop<Parent>)
    : SpecialMethod<Parent>(prop) {

    lateinit var name: String

    override fun prepare(arg: Prop<*>) {
        if (arg.isConstant() && arg.value is String) {
            name = arg.value as String
        }

        throw RuntimeException("Expected arguments (String constant), but found $arg")
    }

    override fun evaluate(): Any {
        val shape = prop.value.findShape(name) ?: throw RuntimeException("Shape $name not found")
        if (shape !== calculatedValue) {
            calculatedValue?.let { unlistenTo((it as Shape).name) }
            listenTo(shape.name)
        }
        return shape
    }
}
