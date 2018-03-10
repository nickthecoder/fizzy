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
import java.util.*

class Document : HasChangeListeners<Document> {

    override val changeListeners = ChangeListeners<Document>()


    private val masterToLocalCopy = mutableMapOf<String, Shape>()

    val id: String = generateDocumentId()

    val pages = MutableFList<Page>()

    /**
     * The set of Master Shapes that are used by this document.
     */
    val localMasterShapes = Page(this, false)


    private var previousId = 0

    private val pagesListener = ChangeAndCollectionListener(this, pages,
            onAdded = { if (it.document != this@Document) throw IllegalStateException("Added a page to the incorrect document") }
    )

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

    fun generateShapeId(): Int {
        previousId++
        return previousId
    }

    /**
     * Takes a master shapes, and returns the local copy of that master shape.
     * If there isn't a copy of the master shape is in [localMasterShapes], then a copy is added.
     */
    fun useMasterShape(masterShape: Shape): Shape {
        val id = masterShape.document().id + ":" + masterShape.id

        var localMaster = masterToLocalCopy[id]
        if (localMaster == null) {
            localMaster = masterShape.copyInto(localMasterShapes, true)
            localMasterShapes.children.add(localMaster)
            masterToLocalCopy[id] = localMaster
        }

        return localMaster
    }

    companion object {

        fun generateDocumentId(): String {
            return UUID.randomUUID().toString()
        }
    }
}
