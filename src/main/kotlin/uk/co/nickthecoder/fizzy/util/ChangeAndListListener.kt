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
package uk.co.nickthecoder.fizzy.util

import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.collection.ListListener

/**
 * Coordinates add/remove/change events from a collection of children (C) to their parent's listeners.
 *
 * When an item is added to the collection, [added] ensures that the parent's [ChangeListeners] are notified
 * by firing a [ChangeListener.changed].
 * At the same time, the parent is added to the child's [ChangeListeners].
 *
 * When an item is removed from the collection, [removed] ensures that the parent's [ChangeListeners] are notified
 * by firing a [ChangeListener.changed].
 * At the same time, the parent is removed from the child's [ChangeListeners].
 *
 * When an item in the collection changes, the parent's [ChangeListener]s are notified by firing a
 * [ChangeListener.changed].
 */
class ChangeAndListListener<P : HasChangeListeners<P>, C : HasChangeListeners<C>>(
        val parent: P,
        child: FList<C>,
        val onAdded: ((item: C, index: Int) -> Unit)? = null,
        val onRemoved: ((item: C, index: Int) -> Unit)? = null
)

    : ChangeListener<C>, ListListener<C> {

    init {
        child.listeners.add(this)
    }

    override fun changed(item: C, changeType: ChangeType, obj: Any?) {
        parent.changeListeners.fireChanged(parent, changeType, obj)
    }

    override fun added(list: FList<C>, item: C, index: Int) {
        parent.changeListeners.fireChanged(parent, ChangeType.ADD, item)
        item.changeListeners.add(this)
        onAdded?.invoke(item, index)
    }

    override fun removed(list: FList<C>, item: C, index: Int) {
        parent.changeListeners.fireChanged(parent, ChangeType.REMOVE, item)
        item.changeListeners.remove(this)
        onRemoved?.invoke(item, index)
    }

}
