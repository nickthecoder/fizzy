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
import uk.co.nickthecoder.fizzy.collection.FList


class FListPropType private constructor()
    : PropType<FList<out Any>>(FList::class) {

    override fun findField(prop: Prop<FList<out Any>>, name: String): Prop<*>? {

        return when (name) {
            "size" -> PropCalculation1(prop) { v -> v.size }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<FList<out Any>>, name: String): PropMethod<FList<out Any>, *>? {
        return null
    }

    companion object {
        val instance = FListPropType()
    }
}

class FListProp<T>(override val value: FList<T>) :
        AbstractProp<FList<*>>(), CollectionListener<T> {

    init {
        value.listeners.add(this)
    }

    override fun added(collection: FCollection<T>, item: T) {
        listeners.fireDirty(this)
    }

    override fun removed(collection: FCollection<T>, item: T) {
        listeners.fireDirty(this)
    }
    // TODO This won't notify listeners when an item in the list CHANGES.
    // Is that a problem?
}
