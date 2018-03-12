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

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.PropExpression

/**
 * Converts a [Document] object into a JSON representation.
 *
 * Note. This class uses a quite verbose, and manual process to convert the model into JSON.
 * I prefer this to the JSON frameworks that can automate the process, because I believe this way is simpler to
 * understand, and more importantly, I have fine grained control over the process, and can more easily ensure
 * backwards compatibility.
 *
 * Most of a Fizzy model is described by [PropExpression]s, and [MetaData] is used to save all of the [PropExpression]s
 * in one go.
 *
 * Conventions used within this class :
 * All JSON objects, such as [jsonObject] and [JsonArray] etc use a "j" prefix on their variable names.
 * Plural names, such as "jPages" are [JsonArray]s.
 */
class DocumentToJson(val document: Document) {

    fun toJson(): JsonObject {
        val jRoot = JsonObject()
        jRoot.add("version", VERSION)

        saveDocument(jRoot)
        return jRoot
    }

    private fun saveDocument(jRoot: JsonObject) {
        val jDocument = JsonObject()
        jRoot.add("document", jDocument)

        jDocument.add("id", document.id)
        // Document data goes here (Document currently has no data (other than its ID)

        // Any master shapes used by the document are saved first
        saveLocalMasters(jDocument)

        // Now all of the pages
        val jPages = JsonArray()
        jDocument.add("pages", jPages)
        document.pages.forEach { page ->
            savePage(jPages, page)
        }
    }

    private fun saveLocalMasters(jRoot: JsonObject) {
        if (document.masterToLocalCopy.isEmpty()) return

        val jLocalMasters = JsonArray()
        jRoot.add("localMasters", jLocalMasters)

        document.masterToLocalCopy.forEach { key, shape ->
            val jLocalMaster = JsonObject()
            jLocalMaster.add("id", key)
            val jShape = JsonObject()
            jLocalMaster.add("shape", jShape)
            saveShape(jShape, shape)
        }
    }

    private fun savePage(jPages: JsonArray, page: Page) {
        val jPage = JsonObject()
        jPages.add(jPage)
        // Page data goes here (Page currently has no data of its own!)

        val jShapes = JsonArray()
        jPage.add("shapes", jShapes)

        page.children.forEach { shape ->
            val jShape = JsonObject()
            jShapes.add(jShape)
            saveShape(jShape, shape)
        }
    }

    private fun saveShape(jShape: JsonObject, shape: Shape) {
        jShape.add("id", shape.id)
        jShape.add("type", shape.javaClass.simpleName)

        val metaData = shape.metaData()
        saveMetaData(jShape, metaData)

        if (shape.children.isNotEmpty()) {
            val jChildren = JsonArray()
            jShape.add("children", jChildren)
            shape.children.forEach { child ->
                val jChild = JsonObject()
                jChildren.add(jChild)
                saveShape(jChild, child)
            }
        }
    }

    private fun saveMetaData(jParent: JsonObject, metaData: MetaData) {

        if (metaData.cells.isNotEmpty()) {
            val jCells = JsonArray()
            jParent.add("cells", jCells)
            metaData.cells.forEach { cell ->
                val jCell = JsonObject()
                jCells.add(jCell)
                jCell.add("name", cell.cellName)
                saveCell(jCell, cell)
            }
        }

        if (metaData.rows.isNotEmpty()) {
            val jRows = JsonArray()
            jParent.add("rows", jRows)
            metaData.rows.forEach { row ->
                val jRow = JsonObject()
                row.type?.let { jRow.add("type", it) }
                jRows.add(jRow)
                saveMetaData(jRow, row)
            }
        }

        if (metaData.sections.isNotEmpty()) {
            val jSections = JsonArray()
            jParent.add("sections", jSections)

            metaData.sections.forEach { sectionName, section ->
                if (!section.isEmpty()) {
                    val jSection = JsonObject()
                    jSections.add(jSection)
                    jSection.add("Section", sectionName)
                    saveMetaData(jSection, section)
                }
            }
        }
    }

    private fun saveCell(jCell: JsonObject, cell: MetaDataCell) {
        val cellProp = cell.cellProp
        if (cellProp is PropExpression<*>) {
            jCell.add("f", cellProp.formula)
            jCell.add("v", cellProp.valueString())
        } else {
            val value = cellProp.value
            if (value is String) {
                jCell.add("s", value)
            } else {
                throw IllegalStateException("Expected a PropExpression or Prop<String>, but found $cellProp")
            }
        }
    }

    companion object {
        /**
         * While in the early stages of development, the version will stay at 0.1, and no attempt will be made
         * to maintain backwards compatibility. Once I'm happy that the format is reasonably stable, I will
         * change the version to 1.0, and then I will maintain backwards compatibility (but not with version 0.1).
         */
        val VERSION = 0.1
    }

}
