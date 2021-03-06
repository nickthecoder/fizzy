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

import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.collection.ListListener
import uk.co.nickthecoder.fizzy.model.Scratch
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop

/**
 * A method for getting to the value of a [Scratch]. To use this, the syntax is :
 *   this.findScratch( "NameOfScratch" )
 *
 * This is cumbersome, and there is now an easier syntax :
 *   this.Scratch.NameOfScratch
 *
 * This replacement uses FindScratchField and a special type for the scratch list, which can then
 * have its own ScratchListPropType.
 */
class FindScratch(prop: Prop<Shape>)
    : TypedMethod1<Shape, String>(prop, String::class.java), ListListener<Scratch> {

    override fun added(list: FList<Scratch>, item: Scratch, index: Int) {
        dirty = true
    }

    override fun removed(list: FList<Scratch>, item: Scratch, index: Int) {
        dirty = true
    }

    private var oldValue: Any? = null

    override fun eval(a: String): Any {
        val scratch = prop.value.findScratch(a)
        setScratch(scratch)
        oldValue = scratch?.expression?.value
        return scratch?.expression?.value ?: throw RuntimeException("Scratch $a not found")
    }

    override fun dirty(prop: Prop<*>) {
        super.dirty(prop)
    }

    var prevScratch: Scratch? = null

    /**
     * If the name of the scratch changes, then we need to become dirty, because the name may now refer to a
     * different Scratch (or the expression should throw, because the named Scratch no longer exists).
     */
    fun setScratch(scratch: Scratch?) {
        if (scratch !== prevScratch) {
            prevScratch?.let {
                unlistenTo(it.name)
                unlistenTo(it.expression)
            }
            scratch?.let {
                listenTo(scratch.name)
                listenTo(scratch.expression)
            }
        }
    }

    override fun toString(): String {
        return "FindScratch"
    }
}
