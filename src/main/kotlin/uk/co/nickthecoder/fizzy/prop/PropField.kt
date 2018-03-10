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

/**
 * Dynamically evaluates a field value of a Prop.
 * This is a dynamically calculated value. Therefore, if the underlying Prop changes its
 * value, the the [PropField] will also update.
 */
class SimplePropField<T : Any>(
        val name: String, // This is only to aid debugging.
        val prop: Prop<T>,
        val lambda: (Prop<T>) -> Any)

    : PropCalculation<Any>() {

    init {
        prop.propListeners.add(this)
    }

    override fun isConstant() = prop.isConstant()

    override fun eval(): Any = lambda(prop)

    override fun toString(): String {
        return "{ SimplePropField $name of ${prop} }"
    }
}

class PropField<T : Any>(
        val name: String, // This is only to aid debugging.
        val prop: Prop<T>,
        val lambda: (Prop<T>) -> Prop<Any>)

    : PropCalculation<Any>() {

    var oldProp: Prop<Any>? = null

    init {
        prop.propListeners.add(this)
    }

    override fun isConstant() = prop.isConstant()

    override fun eval(): Any {
        val result = lambda(prop)
        if (result != oldProp) {
            oldProp?.let { unlistenTo(it) }
            listenTo(result)
        }
        return result.value
    }

    override fun toString(): String {
        return "{ Field $name of ${prop} }"
    }
}
