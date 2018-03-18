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

import uk.co.nickthecoder.fizzy.collection.ListListener
import uk.co.nickthecoder.fizzy.collection.FList
import java.util.regex.Pattern

abstract class PropType<T : Any>(val klass: Class<*>) {

    open fun findField(prop: Prop<T>, name: String): Prop<*>? {

        // To make accessing list properties easier, we can give the list's property name, and the number of the item
        // within the list as one identifier.
        // e.g. myShape.Geometry1
        // Will look for a property called "Geometry" in myShape, and if it is an FList, then find row number 1
        // (which is index 0).
        //
        // Note, there is similar functionality in FListPropType, but it does something different!
        val matcher = nameNumber.matcher(name)
        if (matcher.matches()) {
            val fieldName = matcher.group(1)
            val row = matcher.group(2).toInt()

            val field = prop.field(fieldName)
            if (field != null) {
                val fieldValue = field.value
                if (fieldValue is FList<*>) {
                    if (row > 0 && row <= fieldValue.size) {
                        @Suppress("UNCHECKED_CAST")
                        return ListIndexAccess(fieldValue as FList<Any>, row - 1)
                    }
                }
            }
        }

        return null
    }

    private fun findField2(prop: Prop<*>, name: String): Prop<*>? {
        @Suppress("UNCHECKED_CAST")
        return findField(prop as Prop<T>, name)
    }

    open fun findMethod(prop: Prop<T>, name: String): PropMethod<in T>? = null

    private fun findMethod2(prop: Prop<*>, name: String): PropMethod<in T>? {
        @Suppress("UNCHECKED_CAST")
        return findMethod(prop as Prop<T>, name)
    }

    companion object {
        val propertyTypes = mutableMapOf<Class<*>, PropType<*>>()

        private val nameNumber = Pattern.compile("(.*?)([0-9]+)")

        fun field(prop: Prop<*>, fieldName: String): Prop<*>? {
            return propertyTypes[prop.value.javaClass]?.findField2(prop, fieldName)
        }

        fun method(prop: Prop<*>, methodName: String): PropMethod<*>? {
            return propertyTypes[prop.value.javaClass]?.findMethod2(prop, methodName)
        }

        fun put(propertyType: PropType<*>) {
            propertyTypes.put(propertyType.klass, propertyType)
        }

        fun put(propertyType: PropType<*>, klass: Class<*>) {
            propertyTypes.put(klass, propertyType)
        }
    }
}


class ListIndexAccess(val list: FList<Any>, val index: Int)
    : PropCalculation<Any>(), ListListener<Any> {

    init {
        list.listeners.add(this)
    }

    var oldProp: Prop<*>? = null

    override fun added(list: FList<Any>, item: Any, index: Int) {
        dirty = true
    }

    override fun removed(list: FList<Any>, item: Any, index: Int) {
        dirty = true
    }

    override fun eval(): Any {
        val item = list[index]
        // The "IF" is to allow for list lists of Props or lists of actual values.
        return if (item is Prop<*>) {
            if (oldProp != item) {
                oldProp?.let { unlistenTo(it) }
                listenTo(item)
                oldProp = item
            }
            item.value
        } else {
            item
        }
    }

    override fun toString(): String {
        return "ListIndexAccess"
    }
}
