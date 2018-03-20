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
package uk.co.nickthecoder.fizzy.collection

import uk.co.nickthecoder.fizzy.util.Listeners

class ListListeners<T>
    : Listeners<ListListener<T>>() {

    fun fireAdded(list: FList<T>, element: T, index: Int) {
        forEach { it.added(list, element, index) }
    }

    fun fireRemoved(list: FList<T>, element: T, index: Int) {
        forEach { it.removed(list, element, index) }
    }
}