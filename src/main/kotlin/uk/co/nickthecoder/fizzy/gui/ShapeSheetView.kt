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
package uk.co.nickthecoder.fizzy.gui

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.prop.PropVariable

class ShapeSheetView(val shape: Shape) : BuildableNode {

    private val vBox = VBox()

    private val scroll = ScrollPane(vBox)

    private val sectionNameToTitledPane = mutableMapOf<String, TitledPane>()

    private val geometryPartsListener = object : CollectionListener<GeometryPart> {
        override fun added(collection: FCollection<GeometryPart>, item: GeometryPart) {
            rebuildSection("Geometry")
        }

        override fun removed(collection: FCollection<GeometryPart>, item: GeometryPart) {
            rebuildSection("Geometry")
        }
    }

    private val connectionPointsListener = object : CollectionListener<ConnectionPoint> {
        override fun added(collection: FCollection<ConnectionPoint>, item: ConnectionPoint) {
            rebuildSection("ConnectionPoint")
        }

        override fun removed(collection: FCollection<ConnectionPoint>, item: ConnectionPoint) {
            rebuildSection("ConnectionPoint")
        }
    }

    private val controlPointsListener = object : CollectionListener<ControlPoint> {
        override fun added(collection: FCollection<ControlPoint>, item: ControlPoint) {
            rebuildSection("ControlPoint")
        }

        override fun removed(collection: FCollection<ControlPoint>, item: ControlPoint) {
            rebuildSection("ControlPoint")
        }
    }

    private val scratchListener = object : CollectionListener<Scratch> {
        override fun added(collection: FCollection<Scratch>, item: Scratch) {
            rebuildSection("Scratch")
        }

        override fun removed(collection: FCollection<Scratch>, item: Scratch) {
            rebuildSection("Scratch")
        }
    }

    init {
        shape.geometry.parts.listeners.add(geometryPartsListener)
        shape.connectionPoints.listeners.add(connectionPointsListener)
        shape.controlPoints.listeners.add(controlPointsListener)
        shape.scratches.listeners.add(scratchListener)
    }

    override fun build(): Node {
        vBox.styleClass.add("shape-sheet")

        val metaData = shape.metaData()
        buildSection("Shape ${shape.id}", metaData)

        metaData.sections.forEach { name, section ->
            buildSection(name, section)
        }

        return scroll
    }

    fun buildSection(name: String, metaData: MetaData) {

        val titledPane = TitledPane(name, Label())
        titledPane.styleClass.add("sheet-section")
        sectionNameToTitledPane[name] = titledPane

        buildSection(name, titledPane, metaData)
        vBox.children.add(titledPane)
    }

    fun rebuildSection(sectionName: String) {
        val metaData = shape.metaData()

        metaData.sections.forEach { name, section ->
            if (name == sectionName) {
                sectionNameToTitledPane[name]?.let { titledPane ->
                    buildSection(name, titledPane, section)
                }
            }
        }
    }

    private fun buildSection(sectionName: String, titledPane: TitledPane, metaData: MetaData) {

        val namedCellsAndRows = VBox()
        namedCellsAndRows.styleClass.addAll("namedCellsAndRows")

        if (metaData.cells.isNotEmpty()) {
            val namedCells = GridPane()
            namedCells.styleClass.add("named-cells")
            var index = 0
            metaData.cells.forEach { cell ->
                val control = createControl(cell)
                val label = Label(cell.cellName)
                namedCells.addRow(index, label, control)
                index++
            }
            namedCellsAndRows.children.add(namedCells)
        }

        if (metaData.rows.isNotEmpty()) {
            val rowCells = GridPane()
            rowCells.styleClass.add("row-cells")
            val columnIndices = mutableMapOf<String, Int>()

            metaData.rows.forEachIndexed { rowIndex, row ->
                val rowControls = mutableMapOf<Int, Node>()

                val type = row.type
                if (type != null) {
                    if (!columnIndices.containsKey("type")) {
                        columnIndices["type"] = columnIndices.size
                    }
                    rowControls.put(columnIndices["type"]!!, Label(type))
                }

                row.cells.forEach { cell ->
                    if (!columnIndices.containsKey(cell.cellName)) {
                        columnIndices[cell.cellName] = columnIndices.size
                    }
                    val control = createControl(cell)
                    rowControls.put(columnIndices[cell.cellName]!!, control)
                }
                val controls = Array<Node>(columnIndices.size + 1) { Label() }
                controls[0] = Label("${rowIndex + 1}")
                rowControls.forEach { i, control -> controls[i + 1] = control }
                rowCells.addRow(rowIndex + 1, * controls)
            }
            val headers = Array<Node>(columnIndices.size + 1) { Label() }
            columnIndices.forEach { columnName, columnIndex ->
                val label = Label(columnName)
                label.styleClass.add("header")
                headers[columnIndex + 1] = label
            }
            rowCells.addRow(0, * headers)

            namedCellsAndRows.children.add(rowCells)
        }
        if (metaData.rowFactories.isNotEmpty()) {
            if (metaData.rowFactories.size == 1) {
                val button = Button(metaData.rowFactories[0].label)
                button.onAction = EventHandler {
                    metaData.rowFactories[0].create(metaData.rows.size)
                }
                namedCellsAndRows.children.add(button)
            } else {
                val button = MenuButton("New Row")
                metaData.rowFactories.forEach { factory ->
                    val menuItem = MenuItem(factory.label)
                    menuItem.onAction = EventHandler {
                        factory.create(metaData.rows.size)
                    }
                    button.items.add(menuItem)
                }
                namedCellsAndRows.children.add(button)
            }
        }

        titledPane.content = namedCellsAndRows
    }

    fun createControl(cell: MetaDataCell): Node {
        val exp = cell.cellProp
        if (exp is PropExpression<*>) {
            return ExpressionEditor(exp)
        } else if (exp is PropVariable<*> && exp.value is String) {
            @Suppress("UNCHECKED_CAST")
            return StringEditor(exp as PropVariable<String>)
        } else {
            return Label(cell.cellProp.value.toString())
        }
    }
}

