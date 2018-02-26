package uk.co.nickthecoder.fizzy.gui

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.Window
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MyTabPane

class MainWindow(val stage: Stage) : Window() {

    val borderPane = BorderPane()

    val toolBar = ToolBar()

    val tabs = MyTabPane<DocumentTab>()

    init {
        stage.title = "Fizzy"

        borderPane.top = toolBar
        borderPane.center = tabs

        toolBar.items.add(Label("Toolbar"))
        stage.scene = Scene(borderPane, 800.0, 600.0)
        ParaTask.style(stage.scene)

        stage.show()
    }

    fun addDocument(doc: Document) {
        tabs.add(DocumentTab(doc))
    }
}
