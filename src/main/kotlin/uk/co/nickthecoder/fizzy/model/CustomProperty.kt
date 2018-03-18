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
package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.prop.CustomPropertyListPropType
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.prop.PropVariable
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

/**
 * An empty class definition, subclassing MutableFList<CustomProperty>.
 * See [CustomPropertyListPropType] for details on why this exists.
 */
class CustomPropertyList : MutableFList<CustomProperty>()

class CustomProperty(name: String, label: String = name, data: String)
    : HasChangeListeners<CustomProperty>, PropListener, MetaDataAware {

    override val changeListeners = ChangeListeners<CustomProperty>()

    val name = PropVariable(name)

    val label = PropVariable(label)

    val data = PropVariable(data)

    init {
        this.name.propListeners.add(this)
        this.label.propListeners.add(this)
        this.data.propListeners.add(this)
    }

    override fun metaData(): MetaData {
        val md = MetaData(null)
        addMetaData(md)
        return md
    }

    fun addMetaData(metaData: MetaData) {
        metaData.newCell("Name", name)
        metaData.newCell("Label", label)
        metaData.newCell("Value", data)
    }

    override fun dirty(prop: Prop<*>) {
        changeListeners.fireChanged(this)
    }

    fun copy() = CustomProperty(name = name.value, label = label.value, data = data.value)
}
