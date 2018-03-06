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
    : PropType<FList<Any>>(FList::class) {

    override fun findField(prop: Prop<FList<Any>>, name: String): Prop<*>? {
        return when (name) {
            "Size" -> PropCalculation1(prop) { v -> v.size.toDouble() }
            else -> return findField2(prop, name) ?: super.findField(prop, name)
        }
    }

    private fun findField2(listProp: Prop<FList<Any>>, name: String): Prop<*>? {

        // To make accessing items in an array easier, we can reference a property "Foo" of an item in the list using :
        // "myListProp.FooN" where N is the index into the list with base 1 (i.e. the 0th item is N=1).
        // For example, to get the point from geometry part row 3 (which is index 2) :
        // someGeometry.Point3
        //
        // Note, there is similar functionality in PropType, but it does something different!
        val matcher = nameNumber.matcher(name)
        if (matcher.matches()) {
            val fieldName = matcher.group(1)
            val row = matcher.group(2).toInt()
            if (row > 0 && row <= listProp.value.size) {
                return ListPropertyAccess(listProp, row - 1, fieldName)
            }
        }

        return null
    }

    companion object {
        val instance = FListPropType()

        private val nameNumber = Pattern.compile("(.*?)([0-9]+)")
    }
}

class ListPropertyAccess(val listProp: Prop<FList<Any>>, val index: Int, val fieldName: String)
    : PropCalculation<Any>(), CollectionListener<Any> {

    var oldField: Prop<*>? = null

    override val propListenerOwner = "ListPropertyAccess"

    init {
        listProp.value.listeners.add(this)
    }

    override fun added(collection: FCollection<Any>, item: Any) {
        dirty = true
    }

    override fun removed(collection: FCollection<Any>, item: Any) {
        dirty = true
    }

    override fun eval(): Any {
        val item = listProp.value[index]
        // The "IF" is to allow for list lists of Props or lists of actual values.
        // I think we only use lists of values now, not lists of Props.
        val field = if (item is Prop<*>) {
            PropType.field(PropValue(item.value), fieldName)
        } else {
            PropType.field(PropValue(item), fieldName)
        }

        field ?: throw RuntimeException("Field $fieldName not found")

        if (oldField != field) {
            oldField?.let { unlistenTo(it) }
            listenTo(field)
        }
        field.propListeners.add(this)
        return field.value
    }

    override fun toString(): String {
        return "ListPropertyAccess row $fieldName${index + 1} of parent ${listProp}"
    }
}
