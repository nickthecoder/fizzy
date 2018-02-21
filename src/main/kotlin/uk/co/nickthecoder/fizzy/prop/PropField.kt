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
 * Dynamically evaluates a field value of a Prop, for example, a Prop<Angle>, ".degrees" and ".radians" will create a
 * [PropField] object of type Prop<Double>.
 * This is a dynamically calculated value. Therefore, if the underlying Prop<Angle> changes its
 * value, the the [PropField] will also update.
 */
class PropField<T : Any, F : Any>(val prop: Prop<T>, val lambda: (Prop<T>) -> F)
    : PropCalculation<F>() {

    init {
        prop.listeners.add(this)
    }

    override fun eval(): F = lambda(prop)

    override fun toString(): String {
        return "Field value=${safeValue()}"
    }
}
