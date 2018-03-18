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

import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.collection.ListListener
import uk.co.nickthecoder.fizzy.model.Scratch
import uk.co.nickthecoder.fizzy.model.ScratchList
import uk.co.nickthecoder.fizzy.model.Shape

/**
 * Allows for the following syntax :
 *     Scratch.Foo
 * Where "Foo" is the name of a [Scratch].
 * The expression "Scratch" puts [Shape.scratches] onto the stack, and because it is a specialised class ( [ScratchList] ),
 * and not the generic FList<Scratch>, we can set up a PropType for this scenario.
 *
 * [FindScratchField] does the tricky part, listening for the properties etc.
 */
class ScratchListPropType private constructor()
    : PropType<ScratchList>(ScratchList::class.java) {

    override fun findField(prop: Prop<ScratchList>, name: String): Prop<*>? {
        // Lets us access a scratch value using : Scratch.TheScratchName
        prop.value.forEach { scratch ->
            if (scratch.name.value == name) {
                return FindScratchField(prop.value, name)
            }
        }
        return super.findField(prop, name)
    }

    companion object {
        val instance = ScratchListPropType()
    }
}

class FindScratchField(val scratchList: ScratchList, val name: String)
    : PropCalculation<Any>(), ListListener<Scratch> {

    /**
     * If the scratch is removed, we need to become dirty!
     */
    override fun removed(list: FList<Scratch>, item: Scratch, index: Int) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * If the name has been added back again, become dirty (probably not needed, but it won't hurt).
     */
    override fun added(list: FList<Scratch>, item: Scratch, index: Int) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * Listen to the name, so that if it changes, we become dirty (and will throw when re-evaluated).
     */
    var scratchNameProp: Prop<String>? = null
        set(v) {
            if (field != v) {
                field?.let { unlistenTo(it) }
                v?.let { listenTo(it) }
                field = v
            }
        }

    var scratchExpression: PropExpression<*>? = null
        set(v) {
            field?.propListeners?.remove(this)
            v?.propListeners?.add(this)
        }

    override fun eval(): Any {
        scratchList.forEach { scratch ->
            if (scratch.name.value == name) {
                scratchExpression = scratch.expression
                scratchNameProp = scratch.name
                return scratch.expression.value
            }
        }
        throw RuntimeException("Scratch $name not found")
    }

}
