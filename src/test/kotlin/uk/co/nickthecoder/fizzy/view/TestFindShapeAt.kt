package uk.co.nickthecoder.fizzy.view

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.util.MyShapeTest
import uk.co.nickthecoder.fizzy.util.MyTestCase

/**
 * Tests [Page.findShapeAt] and also [Shape.isAt].
 */
class TestFindShapeAt : MyTestCase(), MyShapeTest {

    @Test
    fun testSimpleBox() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2(20mm,40mm)", "Dimension2(40mm, 120mm)")
        // Box from 30mm, 100m to 50mm, 140mm


        assertTrue(box.isAt(dimension2("Dimension2(40mm,100mm)")))
        assertEquals(box, page.findShapeAt(dimension2("Dimension2(40mm,100mm)")))
        assertNull(page.findShapeAt(dimension2("Dimension2(0mm,100mm)"))) // Too left
        assertNull(page.findShapeAt(dimension2("Dimension2(200mm,100mm)"))) // Too right
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,120mm)"))) // Too low
        assertNull(page.findShapeAt(dimension2("Dimension2(40mm,80mm)"))) // Too high
    }

}
