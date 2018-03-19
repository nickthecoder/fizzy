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
import uk.co.nickthecoder.fizzy.model.fontFactory
import uk.co.nickthecoder.fizzy.prop.expressionExceptionHandler
import uk.co.nickthecoder.fizzy.util.FizzyJsonReader
import uk.co.nickthecoder.fizzy.util.runLaterHandler
import java.io.File

/**
 * The JavaFX [Application] (i.e. the entry point for the program when using a gui.
 */
class Fizzy : Application() {

    override fun start(primaryStage: Stage) {
        runLaterHandler = { Platform.runLater(it) }
        fontFactory = { name, size -> JavaFXFFont(name, size) }
        expressionExceptionHandler = { pe, message -> println("Failed to evaluate ${pe.formula} : $message") }

        val doc = FizzyJsonReader(File("example.fizzy")).load()

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
