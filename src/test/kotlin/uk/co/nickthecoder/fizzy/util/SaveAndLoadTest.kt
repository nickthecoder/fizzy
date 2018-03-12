package uk.co.nickthecoder.fizzy.util

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.DoubleExpression
import java.io.File

class SaveAndLoadTest : MyTestCase() {

    val file = File("test.fizzy")

    override fun setUp() {
        file.delete()
    }

    override fun tearDown() {
        file.delete()
    }

    fun metaDataToString(document: Document): String {
        val buffer = StringBuffer()
        document.pages.forEach { page ->
            page.children.forEach { child ->
                buffer.append(child.metaData().toString())
                buffer.append("\n\n")
            }
        }
        return buffer.toString()
    }

    @Test
    fun testSimple() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page)
        page.children.add(box)

        val metaData = metaDataToString(doc)
        FizzyJsonWriter(doc, file).save()

        val loadedDoc = FizzyJsonReader(file).load()
        assertEquals(metaData, metaDataToString(loadedDoc))
    }


    @Test
    fun testConnectionPoints() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page)
        page.children.add(box)
        box.connectionPoints.add(ConnectionPoint("Dimension2(2m + 1m, 3m + 1m)"))
        box.connectionPoints.add(ConnectionPoint("Dimension2(6cm, 8cm)"))

        val metaData = metaDataToString(doc)
        FizzyJsonWriter(doc, file).save()

        val loadedDoc = FizzyJsonReader(file).load()
        assertEquals(metaData, metaDataToString(loadedDoc))
    }

    @Test
    fun testControlPoints() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page)
        page.children.add(box)
        box.controlPoints.add(ControlPoint("Dimension2(2m + 1m, 3m + 1m)"))
        box.controlPoints.add(ControlPoint("Dimension2(6cm, 8cm)"))

        val metaData = metaDataToString(doc)
        FizzyJsonWriter(doc, file).save()

        val loadedDoc = FizzyJsonReader(file).load()
        assertEquals(metaData, metaDataToString(loadedDoc))
    }

    @Test
    fun testScratches() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page)
        page.children.add(box)
        box.scratches.add(Scratch("foo", Dimension2Expression("Dimension2(2m + 1m, 3m + 1m)")))
        box.scratches.add(Scratch("bar", DoubleExpression("2")))

        val metaData = metaDataToString(doc)
        FizzyJsonWriter(doc, file).save()

        val loadedDoc = FizzyJsonReader(file).load()
        assertEquals(metaData, metaDataToString(loadedDoc))
    }
}
