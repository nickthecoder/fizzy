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
import uk.co.nickthecoder.fizzy.model.CustomProperty
import uk.co.nickthecoder.fizzy.model.CustomPropertyList
import uk.co.nickthecoder.fizzy.model.Shape

/**
 * Allows for the following syntax :
 *     CustomProperty.Foo
 * Where "Foo" is the name of a [CustomProperty].
 * The expression "Scratch" puts [Shape.customProperties] onto the stack, and because it is a specialised class ( [CustomPropertyList] ),
 * and not the generic FList<CustomProperty>, we can set up a PropType for this scenario.
 *
 * [FindCustomeProperty] does the tricky part, listening for the properties etc.
 */
class CustomPropertyListPropType private constructor()
    : PropType<CustomPropertyList>(CustomPropertyList::class.java) {

    override fun findField(prop: Prop<CustomPropertyList>, name: String): Prop<*>? {
        // Lets us access a CustomProperty data value using : CustomProperty.TheName
        prop.value.forEach { property ->
            if (property.name.value == name) {
                return FindCustomeProperty(prop.value, name)
            }
        }
        return super.findField(prop, name)
    }

    companion object {
        val instance = CustomPropertyListPropType()
    }
}

class FindCustomeProperty(val propertyList: CustomPropertyList, val name: String)
    : PropCalculation<Any>(), ListListener<CustomProperty> {

    /**
     * If the scratch is removed, we need to become dirty!
     */
    override fun removed(list: FList<CustomProperty>, item: CustomProperty, index: Int) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * If the name has been added back again, become dirty (probably not needed, but it won't hurt).
     */
    override fun added(list: FList<CustomProperty>, item: CustomProperty, index: Int) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * Listen to the name, so that if it changes, we become dirty (and will throw when re-evaluated).
     */
    var customPropertyName: Prop<String>? = null
        set(v) {
            if (field != v) {
                field?.let { unlistenTo(it) }
                v?.let { listenTo(it) }
                field = v
            }
        }

    var customPropertyData: Prop<*>? = null
        set(v) {
            field?.propListeners?.remove(this)
            v?.propListeners?.add(this)
        }

    override fun eval(): Any {
        propertyList.forEach { property ->
            if (property.name.value == name) {
                customPropertyData = property.data
                customPropertyName = property.name
                return property.data.value
            }
        }
        throw RuntimeException("CustomProperty $name not found")
    }

}
