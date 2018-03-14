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

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.Window
import uk.co.nickthecoder.fizzy.Fizzy
import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.util.FizzyJsonReader
import uk.co.nickthecoder.fizzy.util.FizzyJsonWriter

class MainWindow(val stage: Stage) : Window() {

    val borderPane = BorderPane()

    val splitPane = SplitPane()

    val leftPane = VDock()

    val stencils = StencilsView(this)

    val shortcutHelper = ShortcutHelper("MainWindow", borderPane)

    val tabs = TabPane()

    val toolBar = FToolBar(this)


    val document: Document?
        get() = documentTab?.document

    val documentProperty = SimpleObjectProperty<Document?>(null)

    val documentTab: DocumentTab?
        get() {
            val tab = tabs.selectionModel.selectedItem
            if (tab is DocumentTab) {
                return tab
            }
            return null
        }

    val controller: Controller?
        get() {
            return documentTab?.drawingArea?.controller
        }

    val shapeSelectionProperty: Property<FList<Shape>> = SimpleObjectProperty<FList<Shape>>()

    private var selectionListener = object : CollectionListener<Shape> {
        override fun added(collection: FCollection<Shape>, item: Shape) {
            onSelectionChanged()
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            onSelectionChanged()
        }
    }

    /**
     * Custom colors are shared by all FColorPickers, so this is the place to store them all.
     */
    val customColors = FXCollections.observableArrayList<Color>()

    init {
        stage.title = "Fizzy"

        borderPane.top = toolBar.build()
        borderPane.center = splitPane

        splitPane.items.addAll(leftPane.build(), tabs)
        SplitPane.setResizableWithParent(splitPane.items[0], false)
        splitPane.setDividerPositions(0.25)

        leftPane.add(stencils.build())

        stage.scene = Scene(borderPane, 800.0, 600.0)
        Fizzy.style(stage.scene)

        tabs.selectionModel.selectedItemProperty().addListener { _, oldTab, newTab -> onTabChanged(oldTab, newTab) }
        stage.show()
    }

    fun onTabChanged(oldTab: Tab?, newTab: Tab?) {
        controller?.selection?.let {
            shapeSelectionProperty.value = it
            if (oldTab is DocumentTab) {
                oldTab.drawingArea.controller.selection.listeners.remove(selectionListener)
            }
            if (newTab is DocumentTab) {
                newTab.drawingArea.controller.selection.listeners.add(selectionListener)
            }
            onSelectionChanged()
        }
        documentProperty.value = document
    }

    fun onSelectionChanged() {
        shapeSelectionProperty.value = null
        shapeSelectionProperty.value = controller?.selection
    }

    fun addDocument(doc: Document) {
        val file = doc.file
        val tab = DocumentTab(doc, if (file == null) "New Document" else file.nameWithoutExtension)
        tabs.tabs.add(tab)
        tabs.selectionModel.select(tab)
    }

    fun debug() {
        var foundStale = false
        controller?.selection?.forEach { shape ->
            foundStale = foundStale || shape.debugCheckStale()
        }

        if (!foundStale) {
            println("None stale\n")
            controller?.selection?.forEach { shape ->
                println("---------------");
                println(shape.metaData())
            }
        }
    }

    fun editLocalMasters() {
        document?.let {
            tabs.tabs.add(DocumentTab(it, "Masters", it.localMasterShapes))
        }
    }

    fun editMaster(shape: Shape) {
        val tab = DocumentTab(shape.document(), "Master", shape.page(), shape)
        tabs.tabs.add(tab)
        tabs.selectionModel.select(tab)
    }

    fun editShapeSheet(shape: Shape) {
        val tab = ShapeSheetTab(shape)
        tabs.tabs.add(tab)
        tabs.selectionModel.select(tab)
    }

    fun documentNew() {
        val doc = Document()
        doc.pages.add(Page(doc))
        addDocument(doc)
    }


    fun documentOpen() {
        val chooser = FileChooser()
        chooser.extensionFilters.add(fizzyExtensionFilter)
        chooser.extensionFilters.add(stencilExtensionFilter)
        val file = chooser.showOpenDialog(this)
        if (file != null) {
            addDocument(FizzyJsonReader(file).load())
        }
    }

    fun documentSave() {
        val doc = document ?: return
        val file = doc.file
        if (file == null) {
            documentSaveAs()
        } else {
            FizzyJsonWriter(doc, file).save()
        }
    }

    fun documentSaveAs() {

        val doc = document ?: return
        val chooser = FileChooser()
        chooser.extensionFilters.add(fizzyExtensionFilter)
        chooser.extensionFilters.add(stencilExtensionFilter)
        val file = chooser.showSaveDialog(this)
        if (file != null) {
            FizzyJsonWriter(doc, file).save()
        }
    }

    fun closeTab() {
        tabs.selectionModel.selectedItem?.let { tabs.tabs.remove(it) }
    }

    companion object {
        val fizzyExtensionFilter = FileChooser.ExtensionFilter("Fizzy Document (*.fizzy)", "*.fizzy")
        val stencilExtensionFilter = FileChooser.ExtensionFilter("Fizzy Stencil (*.fstencil)", "*.fstencil")

    }
}
