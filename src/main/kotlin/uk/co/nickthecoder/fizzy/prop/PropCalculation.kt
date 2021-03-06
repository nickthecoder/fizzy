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
                    propListeners.fireDirty(this)
                }
            }
        }

    protected var calculatedValue: T? = null

    override fun dirty(prop: Prop<*>) {
        dirty = true
    }

    private var isCalculating = false

    override val value: T
        get() : T {
            if (dirty) {
                if (isCalculating) {
                    throw RuntimeException("Recursive evaluation of $this")
                }
                isCalculating = true
                try {
                    calculatedValue = eval()
                } finally {
                    isCalculating = false
                    dirty = false
                }
            }
            return calculatedValue!!
        }

    abstract fun eval(): T

    fun listenTo(prop: Prop<*>) {
        if (!prop.isConstant()) {
            prop.propListeners.add(this)
        }
    }

    fun unlistenTo(prop: Prop<*>) {
        if (!prop.isConstant()) {
            prop.propListeners.remove(this)
        }
    }

    override fun toString(): String = "${this.javaClass.simpleName} ${if (isConstant()) value.toString() else ""}"
}

class PropCalculation1<T : Any, A : Any>(val a: Prop<A>, val lambda: (A) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.propListeners.add(this)
    }

    override fun eval(): T = lambda(a.value)
}

class PropCalculation2<T : Any, A : Any, B : Any>(val a: Prop<A>, val b: Prop<B>, val lambda: (A, B) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.propListeners.add(this)
        if (!b.isConstant()) b.propListeners.add(this)
    }

    override fun eval(): T = lambda(a.value, b.value)
}

class PropCalculation3<T : Any, A : Any, B : Any, C : Any>(val a: Prop<A>, val b: Prop<B>, val c: Prop<C>, val lambda: (A, B, C) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.propListeners.add(this)
        if (!b.isConstant()) b.propListeners.add(this)
        if (!c.isConstant()) c.propListeners.add(this)
    }

    override fun eval(): T = lambda(a.value, b.value, c.value)
}

class PropCalculation4<T : Any, A : Any, B : Any, C : Any, D : Any>(val a: Prop<A>, val b: Prop<B>, val c: Prop<C>, val d: Prop<D>, val lambda: (A, B, C, D) -> T)
    : PropCalculation<T>() {

    init {
        if (!a.isConstant()) a.propListeners.add(this)
        if (!b.isConstant()) b.propListeners.add(this)
        if (!c.isConstant()) c.propListeners.add(this)
        if (!d.isConstant()) c.propListeners.add(this)
    }

    override fun eval(): T = lambda(a.value, b.value, c.value, d.value)
}
