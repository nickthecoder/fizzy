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

    private fun appendShapeToMetaDataString(shape: Shape, buffer: StringBuffer) {
        buffer.append(shape.metaData().toString())
        shape.children.forEach { child ->
            appendShapeToMetaDataString(child, buffer)
        }
    }

    fun metaDataToString(document: Document): String {
        val buffer = StringBuffer()
        document.pages.forEach { page ->
            page.children.forEach { shape ->
                appendShapeToMetaDataString(shape, buffer)
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

    @Test
    fun testChildren() {
        val doc = Document()
        val page = Page(doc)

        val box = createBox(page)
        page.children.add(box)

        val text = Shape.createText(box, "Hello")
        box.children.add(text)

        val metaData = metaDataToString(doc)
        FizzyJsonWriter(doc, file).save()

        val loadedDoc = FizzyJsonReader(file).load()
        assertEquals(metaData, metaDataToString(loadedDoc))
    }
}
