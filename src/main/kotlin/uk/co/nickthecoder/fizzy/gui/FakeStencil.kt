package uk.co.nickthecoder.fizzy.gui

import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape

/**
 * This is a temporary holder of shapes. Its used during devDeleteTool(it)elopment as a replacement for a proper set of stencils.
 */
class FakeStencil {

    val document = Document()

    val page = Page(document)

    val box = Shape.createBox(page, "Dimension2(200mm,100mm)", "Dimension2(0mm,0mm)", fillColor = "Color.yellow")

    val line = Shape.createLine(page, "Dimension2(0mm,mcm)", "Dimension2(10mm,0mm)")

}