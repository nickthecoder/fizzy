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
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.Window
import uk.co.nickthecoder.fizzy.gui.tools.DeleteTool
import uk.co.nickthecoder.fizzy.gui.tools.SelectTool
import uk.co.nickthecoder.fizzy.gui.tools.StampShape2dTool
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MyTabPane

class MainWindow(val stage: Stage) : Window() {

    val borderPane = BorderPane()

    val toolBar = ToolBar()

    val tabs = MyTabPane<DocumentTab>()

    val undoButton = Button("Undo")
    val redoButton = Button("Redo")
    val selectToolButton = Button("Select")
    val deleteToolButton = Button("Delete")
    val boxToolButton = Button("Box")
    val lineToolButton = Button("Line")

    // TODO This is a temporary measure until proper stencils are created.
    val fakeStencil = FakeStencil()

    init {
        stage.title = "Fizzy"

        borderPane.top = toolBar
        borderPane.center = tabs

        toolBar.items.add(Label("Toolbar"))
        stage.scene = Scene(borderPane, 800.0, 600.0)
        ParaTask.style(stage.scene)

        undoButton.onAction = EventHandler { tabs.selectedTab?.document?.history?.undo() }
        redoButton.onAction = EventHandler { tabs.selectedTab?.document?.history?.redo() }
        selectToolButton.onAction = EventHandler { tabs.selectedTab?.drawingArea?.glassCanvas?.let { it.tool = SelectTool(it) } }
        deleteToolButton.onAction = EventHandler { tabs.selectedTab?.drawingArea?.glassCanvas?.let { it.tool = DeleteTool(it) } }
        boxToolButton.onAction = EventHandler { tabs.selectedTab?.drawingArea?.glassCanvas?.let { it.tool = StampShape2dTool(it, fakeStencil.box) } }
        toolBar.items.addAll(undoButton, redoButton, selectToolButton, deleteToolButton, boxToolButton)

        stage.show()
    }

    fun addDocument(doc: Document) {
        tabs.add(DocumentTab(doc))
    }
}
