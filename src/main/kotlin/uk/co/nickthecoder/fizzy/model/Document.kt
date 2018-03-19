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
import uk.co.nickthecoder.fizzy.prop.PropVariable
import uk.co.nickthecoder.fizzy.util.ChangeAndListListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners
import java.io.File
import java.util.*

class Document(val id: String = Document.generateDocumentId())
    : HasChangeListeners<Document> {

    override val changeListeners = ChangeListeners<Document>()

    internal val masterToLocalCopy = mutableMapOf<String, Shape>()

    var file: File? = null
        set(v) {
            field = v
            if (v != null) {
                name.value = v.nameWithoutExtension
            }
        }

    val name = PropVariable<String>("New Document")

    val pages = MutableFList<Page>()

    /**
     * The set of Master Shapes that are used by this document.
     */
    val localMasterShapes = Page(this, false)


    private var previousId = 0

    private val pagesListener = ChangeAndListListener(this, pages,
            onAdded = { item, _ -> if (item.document != this@Document) throw IllegalStateException("Added a page to the incorrect document") }
    )

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
     * Makes a local copy of the master shape. Creates a new shape based on the copy of the master.
     */
    fun copyMasterShape(masterShape: Shape, parent: ShapeParent): Shape {
        val localMaster = useMasterShape(masterShape)
        val copy = localMaster.copyInto(parent, true)
        return copy
    }

    /**
     * Takes a master shapes, and returns the local copy of that master shape.
     * If there isn't a copy of the master shape is in [localMasterShapes], then a copy is added.
     */
    fun useMasterShape(masterShape: Shape): Shape {

        if (masterShape in localMasterShapes.children) {
            return masterShape
        }

        val id = masterShape.document().id + ":" + masterShape.id

        var localMaster = masterToLocalCopy[id]
        if (localMaster == null) {
            localMaster = masterShape.copyInto(localMasterShapes, false)
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
