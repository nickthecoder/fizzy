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
 * The base class for all properties. Properties which have a value which can be changed should subclass
 * [PropConstant], whereas properties who's value changes dynamically (such as PropCalculation) do NOT
 * subclass [PropConstant], and therefore the value is a val.
 *
 * T should be an immutable type, so that the property value cannot change without the property's listeners being
 * notified.
 * Note. Mutable classes such as Shape are also used as [Prop] values, and their listeners never fire.
 * This is ok, because they only use a Prop<Shape> to get to the Prop's that they care about.
 * i.e. the are temporary items within an expression.
 */
interface Prop<out T : Any> {

    val value: T

    val propListeners: PropListeners

    fun isConstant() = false

    fun field(name: String): Prop<*>? = PropType.field(this, name)

    fun method(name: String): Prop<*>? = PropType.method(this, name)

}

abstract class AbstractProp<out T : Any> : Prop<T> {

    override val propListeners = PropListeners()

    override fun toString(): String = "Prop: $value"

}

fun throwExpectedType(type: String, found: Prop<*>): Prop<*> {
    throw RuntimeException("Expected a $type, but found ${found.value.javaClass.simpleName}")
}
