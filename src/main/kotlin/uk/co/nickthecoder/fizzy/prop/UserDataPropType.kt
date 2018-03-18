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
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.UserData
import uk.co.nickthecoder.fizzy.model.UserDataList

/**
 * Allows for the following syntax :
 *     UserData.Foo
 * Where "Foo" is the name of a [UserData].
 * The expression "Scratch" puts [Shape.userDataList] onto the stack, and because it is a specialised class ( [UserDataList] ),
 * and not the generic FList<UserData>, we can set up a PropType for this scenario.
 *
 * [FindUserData] does the tricky part, listening for the properties etc.
 */
class UserDataListPropType private constructor()
    : PropType<UserDataList>(UserDataList::class.java) {

    override fun findField(prop: Prop<UserDataList>, name: String): Prop<*>? {
        // Lets us access a user data value using : UserData.TheName
        prop.value.forEach { property ->
            if (property.name.value == name) {
                return FindUserData(prop.value, name)
            }
        }
        return super.findField(prop, name)
    }

    companion object {
        val instance = UserDataListPropType()
    }
}

class FindUserData(val propertyList: UserDataList, val name: String)
    : PropCalculation<Any>(), ListListener<UserData> {

    /**
     * If the scratch is removed, we need to become dirty!
     */
    override fun removed(list: FList<UserData>, item: UserData, index: Int) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * If the name has been added back again, become dirty (probably not needed, but it won't hurt).
     */
    override fun added(list: FList<UserData>, item: UserData, index: Int) {
        if (item.name.value == name) {
            dirty = true
        }
    }

    /**
     * Listen to the name, so that if it changes, we become dirty (and will throw when re-evaluated).
     */
    var userDataName: Prop<String>? = null
        set(v) {
            if (field != v) {
                field?.let { unlistenTo(it) }
                v?.let { listenTo(it) }
                field = v
            }
        }

    var userDataData: Prop<*>? = null
        set(v) {
            field?.propListeners?.remove(this)
            v?.propListeners?.add(this)
        }

    override fun eval(): Any {
        propertyList.forEach { property ->
            if (property.name.value == name) {
                userDataData = property.data
                userDataName = property.name
                return property.data.value
            }
        }
        throw RuntimeException("UserData $name not found")
    }

}
