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
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import uk.co.nickthecoder.fizzy.gui.JavaFXFFont
import uk.co.nickthecoder.fizzy.gui.MainWindow
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.expressionExceptionHandler
import uk.co.nickthecoder.fizzy.util.runLaterHandler

/**
 * The JavaFX [Application] (i.e. the entry point for the program when using a gui.
 */
class Fizzy : Application() {

    override fun start(primaryStage: Stage) {
        runLaterHandler = { Platform.runLater(it) }
        fontFactory = { name, size -> JavaFXFFont(name, size) }
        expressionExceptionHandler = { pe, message -> println("Failed to evaluate ${pe.formula} : $message") }

        // For now, we create a test document to display.
        val doc = Document()
        val page = Page(doc)

        val box1 = Shape.createBox(page, "Dimension2(20cm,10cm)", "Dimension2(40cm,10cm)")
        val box2 = Shape.createBox(page, "Dimension2(20cm,10cm)", "Dimension2(40cm,30cm)")

        box1.children.add(box1.createText("A very very very long piece of text\nin\na Box1", alignY = 0.0))
        box2.children.add(box2.createText("Lots\nof\nlines\nof\ntext\nin\nBox2", alignX = 0.0))

        val text1 = Shape.createText(page, "Hello\nWorld\nNick\nThe\nCoder", at = "Dimension2(40cm, 20cm)")
        val text2 = Shape.createText(page, "Left\nAlign", at = "Dimension2(30cm, 20cm)", alignX = 0.0)
        val text3 = Shape.createText(page, "Right\nAlign", at = "Dimension2(50cm, 20cm)", alignX = 1.0)

        page.children.add(text1)
        page.children.add(text2)
        page.children.add(text3)

        page.children.add(box1)
        page.children.add(box2)

        box1.connectionPoints.add(ConnectionPoint("(Geometry1.Point1 + Geometry1.Point2) / 2"))
        box1.connectionPoints.add(ConnectionPoint("(Geometry1.Point3 + Geometry1.Point4) / 2"))

        box2.connectionPoints.add(ConnectionPoint("(Geometry1.Point1 + Geometry1.Point2) / 2"))
        box2.connectionPoints.add(ConnectionPoint("(Geometry1.Point3 + Geometry1.Point4) / 2"))

        val line = Shape.createLine(page, "Dimension2(10cm,11cm)", "Dimension2(10cm,13cm)")
        page.children.add(line)

        line.start.formula = "this.connectTo(Page.Shape1.ConnectionPoint2)"
        line.end.formula = "this.connectAlong(Page.Shape2.Geometry1, 0.125)"

        val mainWindow = MainWindow(primaryStage)
        mainWindow.addDocument(doc)
    }

    companion object {

        private val imageMap = mutableMapOf<String, Image?>()

        fun start() {
            Application.launch(Fizzy::class.java)
        }

        fun style(scene: Scene) {
            val resource = Fizzy::class.java.getResource("fizzy.css")
            scene.stylesheets.add(resource.toExternalForm())
        }

        fun imageResource(name: String): Image? {
            val image = imageMap[name]
            if (image == null) {
                val imageStream = Fizzy::class.java.getResourceAsStream(name)
                if (imageStream == null) {
                    println("Didn't find image '$name'")
                }
                val newImage = if (imageStream == null) null else Image(imageStream)
                imageMap.put(name, newImage)
                return newImage
            }
            return image
        }

        fun graphic(name: String): ImageView? = imageResource(name)?.let { ImageView(it) }
    }
}

/**
 *
 */
fun main(args: Array<String>) {

    Fizzy.start()
}
