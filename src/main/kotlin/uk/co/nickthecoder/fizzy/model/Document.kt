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
import uk.co.nickthecoder.fizzy.model.history.History
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class Document : HasChangeListeners<Document> {

    override val changeListeners = ChangeListeners<Document>()

    var pages = MutableFList<Page>()


    private var previousId = 0

    private val pagesListener = ChangeAndCollectionListener(this, pages)

    // TODO When load/save is implemented, this should be the file name without the file extension.
    val name: String
        get() = "New Document"

    val selection = MutableFList<Shape>()

    val history = History()

    fun findShape(id: Int): Shape? {
        pages.forEach { page ->
            val found = page.findShape(id)
            if (found != null) {
                return found
            }
        }
        return null
    }

    fun generateId(): Int {
        previousId++
        return previousId
    }
}
