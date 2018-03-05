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
import java.util.regex.Pattern


class FListPropType private constructor()
    : PropType<FList<out Any>>(FList::class) {

    override fun findField(prop: Prop<FList<out Any>>, name: String): Prop<*>? {
        return when (name) {
            "Size" -> PropCalculation1(prop) { v -> v.size.toDouble() }
            else -> return findField(prop.value, name) ?: super.findField(prop, name)
        }
    }

    fun findField(list: FList<*>, name: String): Prop<*>? {

        // To make accessing items in an array easier, we can reference a property "Foo" of an item in the list using :
        // "myListProp.FooN" where N is the index into the list with base 1 (i.e. the 0th item is N=1).
        // For example, to get the point from geometry part number 3 (which is index 2) :
        // someGeometry.Point3
        //
        // Note, there is similar functionality in PropType, but it does something different!
        val matcher = nameNumber.matcher(name)
        if (matcher.matches()) {
            val fieldName = matcher.group(1)
            val index = matcher.group(2).toInt()
            if (index > 0 && index <= list.size) {
                list [index - 1]?.let { item ->
                    // The "IF" is to allow for list lists of Props or lists of actual values.
                    val field = if (item is Prop<*>) {
                        PropType.field(PropConstant(item.value), fieldName)
                    } else {
                        PropType.field(PropConstant(item), fieldName)
                    }
                    if (field != null) {
                        return field
                    }
                }
            }
        }

        return null
    }

    companion object {
        val instance = FListPropType()

        private val nameNumber = Pattern.compile("(.*?)([0-9]+)")
    }
}

class FListProp<T>(override val value: FList<T>) :
        AbstractProp<FList<*>>(), CollectionListener<T> {

    init {
        value.listeners.add(this)
    }

    override fun added(collection: FCollection<T>, item: T) {
        propListeners.fireDirty(this)
    }

    override fun removed(collection: FCollection<T>, item: T) {
        propListeners.fireDirty(this)
    }
    // TODO This won't notify listeners when an item in the list CHANGES.
    // Is that a problem?
}
