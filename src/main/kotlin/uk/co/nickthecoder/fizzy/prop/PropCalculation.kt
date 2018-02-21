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

abstract class PropCalculation<T : Any>

    : AbstractProp<T>(), PropListener {

    protected var dirty: Boolean = true
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    listeners.fireDirty(this)
                }
            }
        }

    private lateinit var calculatedValue: T

    override fun dirty(prop: Prop<*>) {
        dirty = true
    }

    override val value: T
        get() : T {
            if (dirty) {
                calculatedValue = eval()
                dirty = false
            }
            return calculatedValue
        }

    abstract fun eval(): T

    /**
     * Returns the value, or null if the value could not be calculated.
     */
    fun safeValue(): T? {
        try {
            return value
        } catch (e: Exception) {
            return null
        }
    }

    override fun toString(): String = "${this.javaClass.simpleName}: ${safeValue()}"
}

class PropCalculation1<T : Any, A>(val a: Prop<A>, val lambda: (A) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.listeners.add(this)
    }

    override fun eval(): T = lambda(a.value)
}

class PropCalculation2<T : Any, A, B>(val a: Prop<A>, val b: Prop<B>, val lambda: (A, B) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.listeners.add(this)
        if (!b.isConstant()) b.listeners.add(this)
    }

    override fun eval(): T = lambda(a.value, b.value)
}
