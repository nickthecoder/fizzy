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
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage
import uk.co.nickthecoder.fizzy.Fizzy
import uk.co.nickthecoder.fizzy.model.Shape

class CustomPropertiesWindow(val mainWindow: MainWindow, val shape: Shape) {

    val borderPane = BorderPane()

    init {
        val stage = Stage()
        val scene = Scene(borderPane)
        Fizzy.style(scene)
        stage.scene = scene
        stage.title = "Custom Properties"

        val grid = GridPane()
        borderPane.center = grid

        val buttons = HBox()
        buttons.styleClass.add("buttons")
        val doneButton = Button("Done")
        doneButton.isDefaultButton = true
        doneButton.onAction = EventHandler { stage.close() }
        buttons.children.add(doneButton)
        borderPane.bottom = buttons

        grid.styleClass.add("custom-properties")
        shape.customProperties.forEachIndexed { index, customProperty ->
            val label = Label(customProperty.label.value)
            val data = StringVariableEditor(customProperty.data)
            grid.addRow(index, label, data)
        }

        stage.initModality(Modality.WINDOW_MODAL)
        stage.initOwner(mainWindow)
        stage.showAndWait()
    }

}
