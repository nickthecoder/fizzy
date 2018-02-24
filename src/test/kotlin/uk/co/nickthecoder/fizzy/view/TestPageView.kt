package uk.co.nickthecoder.fizzy.view

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.util.MyShapeTest
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestPageView : MyTestCase(), MyShapeTest {

    val document = Document()
    val page = Page(document)

    @Test
    fun testSimpleBox() {
        val box = createBox(page, "Dimension2(2mm,10mm)", "Dimension2(6mm,28mm)")

        val context = MockContext()
        val view = PageView(page, context)

        view.draw()
        val result = context.toList()
        println(result)
        println("Box size : ${box.size.value}  Pos : ${box.transform.pin}  Center : ${box.transform.locPin.value}")
        assertEquals("M5.0,23.0", result[0])
    }

}
