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
package uk.co.nickthecoder.fizzy

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import uk.co.nickthecoder.fizzy.gui.MainWindow
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.util.runLaterHandler

/**
 * The JavaFX [Application] (i.e. the entry point for the program when using a gui.
 */
class Fizzy : Application() {

    override fun start(primaryStage: Stage) {
        runLaterHandler = { Platform.runLater(it) }

        // For now, we create a test document to display.
        val doc = Document()
        val page = Page(doc)
        Shape.createBox(page, "Dimension2(4cm,2cm)", "Dimension2(10cm,10cm)")
        Shape.createBox(page, "Dimension2(4cm,2cm)", "Dimension2(10cm,14cm)")

        val line = Shape.createLine(page, "Dimension2(10cm,11cm)", "Dimension2(10cm,13cm)")
        line.start.expression = "this.joinTo(1, Page.Shape1.Geometry1.Point1)"
        line.end.expression = "this.joinTo(2, Page.Shape2.Geometry1.Point1)"
        //line.end.expression = "this.joinAlong(Page#1 .Geometry1, 0.125)"

        val mainWindow = MainWindow(primaryStage)
        mainWindow.addDocument(doc)
    }

    companion object {
        fun start() {
            Application.launch(Fizzy::class.java)
        }
    }
}

/**
 *
 */
fun main(args: Array<String>) {

    Fizzy.start()
}
