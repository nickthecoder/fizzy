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
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.prop.PropVariable

class JsonToDocument(val jRoot: JsonObject) {

    var version: Double = 0.0

    private val pathToExp = mutableMapOf<String, PropExpression<*>>()

    fun toDocument(): Document {
        version = jRoot.getDouble("version", 0.0)

        return loadDocument(jRoot)
    }

    private fun cacheLinks(parent: ShapeParent) {
        parent.children.forEach { shape ->
            shape.metaData().buildExpressionMap("${shape.id}.").forEach { exp, path ->
                pathToExp[path] = exp
            }
            cacheLinks(shape)
        }
    }

    fun loadDocument(jRoot: JsonObject): Document {
        val jDocument = jRoot.get("document") as JsonObject

        val id = jDocument.getString("id", Document.generateDocumentId())
        val document = Document(id)

        loadLocalMasters(jDocument, document)
        cacheLinks(document.localMasterShapes)

        val jPages = jDocument.optionalArray("pages")
        jPages.forEach { it ->
            val jPage = it.asObject()
            loadPage(jPage, document)
        }

        if (document.pages.isEmpty()) {
            document.pages.add(Page(document))
        }

        return document
    }

    fun loadLocalMasters(jDocument: JsonObject, document: Document) {
        val jLocalMasters = jDocument.optionalArray("localMasters")
        jLocalMasters.forEach {
            val jLocalMaster = it.asObject()
            val id = jLocalMaster.get("id").asString()
            val jShape = jLocalMaster.get("shape").asObject()
            val shape = loadShape(jShape, document.localMasterShapes)
            document.localMasterShapes.children.add(shape)
            document.masterToLocalCopy[id] = shape
        }

    }

    fun loadPage(jPage: JsonObject, document: Document) {
        val page = Page(document)
        val jShapes = jPage.optionalArray("shapes")
        jShapes.forEach {
            val jShape = it.asObject()
            val shape = loadShape(jShape, page)
            page.children.add(shape)
        }
    }

    fun loadShape(jShape: JsonObject, parent: ShapeParent): Shape {
        val id = jShape.get("id").asInt()
        val type = jShape.get("type").asString()
        val linkedFromId = jShape.get("linkedFrom")

        val linkedFrom: Shape? = if (linkedFromId == null)
            null
        else
            parent.document().localMasterShapes.findShape(linkedFromId.asInt())

        val shape = when (type) {
            "Shape1d" -> Shape1d.create(parent, linkedFrom, id)
            "Shape2d" -> Shape2d.create(parent, linkedFrom, id)
            "ShapeText" -> ShapeText.create(parent, linkedFrom, id)
            else -> throw IllegalStateException("Unknown shape type $type")
        }

        loadMetaData(jShape, shape, null, shape.metaData())

        jShape.optionalArray("children").forEach {
            val jChild = it.asObject()
            shape.children.add(loadShape(jChild, shape))
        }

        return shape
    }

    fun loadMetaData(jParent: JsonObject, parent: MetaDataAware, parentSectionName: String?, metaData: MetaData) {

        var metaDataVar = metaData

        jParent.optionalArray("cells").forEach {
            val jCell = it.asObject()
            val name = jCell.get("name").asString()
            val cell = metaDataVar.findCell(name)
            cell ?: throw IllegalStateException("Unknown cell $name")
            loadCell(jCell, cell)
            // In Scratch, when the type changes, the expression changes (from one type of PropExpression to another)
            // e.g. from DoubleExpression to DimensionExpression.
            // In which case the metaData will refer to the OLD expression, so lets refresh the metaData.
            if (parentSectionName == "Scratch" && name == "Type") {
                metaDataVar = parent.metaData()
            }
        }

        jParent.optionalArray("rows").forEach {
            val jRow = it.asObject()
            val type: String? = jRow.getString("type", parentSectionName)
            val (rowObject, rowMetaData) = parent.createRow(type)
            loadMetaData(jRow, rowObject, parentSectionName, rowMetaData)
        }

        jParent.optionalArray("sections").forEach {
            val jSection = it.asObject()
            val sectionName = jSection.get("Section").asString()
            val (sectionObject, sectionMetaData) = parent.getSection(sectionName)
            if (sectionObject is MetaDataAware) {
                loadMetaData(jSection, sectionObject, sectionName, sectionMetaData)
            } else if (sectionObject is FList<*>) {
                loadMetaData(jSection, parent, sectionName, sectionMetaData)

            }
        }
    }

    fun loadCell(jCell: JsonObject, cell: MetaDataCell) {
        val prop = cell.cellProp
        if (prop is PropExpression<*>) {

            val valueString = jCell.get("v").asString()
            prop.formula = valueString

            val link = jCell.get("l")
            if (link == null) {
                val formula = jCell.get("f").asString()
                prop.formula = formula
            } else {
                val exp = pathToExp[link.asString()]
                        ?: throw RuntimeException("Path ${link.asString()} not found")
                prop.copyFrom(exp, true)
            }
        } else {
            jCell.getString("s", null)?.let {
                if (prop !is PropVariable) throw IllegalStateException("Expected PropVariable but found $prop")
                if (prop.value is String) {
                    @Suppress("UNCHECKED_CAST")
                    (prop as PropVariable<String>).value = it
                }
            }
        }
    }
}

fun JsonObject.optionalArray(name: String): JsonArray {
    val jChild = this.get(name)
    if (jChild == null) {
        return JsonArray()
    } else {
        return jChild.asArray()
    }
}