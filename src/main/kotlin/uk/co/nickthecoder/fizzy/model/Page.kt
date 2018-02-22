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

class Page : HasChangeListeners<Page> {

    override val listeners = ChangeListeners<Page>()

    var layers = MutableFList<Layer>()


    private var previousId = 0

    private val layersListener = ChangeAndCollectionListener(this, layers)

    fun findShape(id: String): Shape? {
        layers.forEach { layer ->
            val found = layer.findShape(id)
            if (found != null) {
                return found
            }
        }
        return null
    }

    fun generateId(): String {
        previousId++
        return "shape$previousId"
    }
}
