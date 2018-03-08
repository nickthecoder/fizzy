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
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import uk.co.nickthecoder.fizzy.Fizzy
import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Shape

class MainWindow(val stage: Stage) : Window() {

    val borderPane = BorderPane()

    val shortcutHelper = ShortcutHelper("MainWindow", borderPane)

    val tabs = TabPane()

    val toolBar = FToolBar(this)


    val document: Document?
        get() = documentTab?.document

    val documentTab: DocumentTab?
        get() {
            val tab = tabs.selectionModel.selectedItem
            if (tab is DocumentTab) {
                return tab
            }
            return null
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
        borderPane.center = tabs

        stage.scene = Scene(borderPane, 800.0, 600.0)
        Fizzy.style(stage.scene)

        tabs.selectionModel.selectedItemProperty().addListener { _, oldTab, newTab -> onTabChanged(oldTab, newTab) }
        stage.show()
    }

    fun onTabChanged(oldTab: Tab?, newTab: Tab) {
        document?.selection?.let {
            shapeSelectionProperty.value = it
            if (oldTab is DocumentTab) {
                oldTab.document.selection.listeners.remove(selectionListener)
            }
            if (newTab is DocumentTab) {
                newTab.document.selection.listeners.add(selectionListener)
            }
            onSelectionChanged()
        }
    }

    fun onSelectionChanged() {
        shapeSelectionProperty.value = null
        shapeSelectionProperty.value = document?.selection
    }

    fun addDocument(doc: Document) {
        tabs.tabs.add(DocumentTab(doc))
    }

    fun debug() {
        document?.let { document ->

            var foundStale = false
            document.pages[0].children.forEach { shape ->
                foundStale = foundStale || shape.debugCheckStale()
            }

            if (!foundStale) {
                println("None stale\n")
                document.pages[0].children.forEach { shape ->
                    println("---------------");
                    println(shape.metaData().joinToString(separator = "\n"))
                }
            }
        }

    }
}
