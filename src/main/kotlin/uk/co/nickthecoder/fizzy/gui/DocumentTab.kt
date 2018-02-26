package uk.co.nickthecoder.fizzy.gui

import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.paratask.gui.MyTab

class DocumentTab(doc: Document)
    : MyTab(doc.name) {

    init {
        if (doc.pages.size == 0) {
            doc.pages.add(Page(doc))
        }
    }

    val drawingArea = DrawingArea(doc.pages[0])

    val document: Document
        get() = drawingArea.page.document

    val page: Page
        get() = drawingArea.page

    init {
        content = drawingArea.build()
    }

}
