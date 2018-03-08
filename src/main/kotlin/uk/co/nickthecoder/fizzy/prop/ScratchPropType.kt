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

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.model.Scratch
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class ScratchList : MutableFList<Scratch>()

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
    : PropCalculation<Any>(), CollectionListener<Scratch> {

    override val propListenerOwner = "FindScratchField"

    /**
     * If the scratch is removed, we need to become dirty!
     */
    override fun removed(collection: FCollection<Scratch>, item: Scratch) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * If the name has been added back again, become dirty (probably not needed, but it won't hurt).
     */
    override fun added(collection: FCollection<Scratch>, item: Scratch) {
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

    override fun eval(): Any {
        scratchList.forEach { scratch ->
            if (scratch.name.value == name) {
                scratchNameProp = scratch.name
                return scratch.expression.value
            }
        }
        throw RuntimeException("Scratch $name not found")
    }

}
