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
        createBox(page, "Dimension2(4mm,10mm)", "Dimension2(6mm,28mm)")

        val context = MockContext()
        val view = PageView(page, context)

        view.draw()
        assertEquals("""
            |M4,23
            |L8,23
            |L8,33
            |L4,33
            |L4,23


        """.trimMargin(), context.toString())
    }

    @Test
    fun testScaledBox() {
        val box1 = createBox(page, "Dimension2(120mm,240mm)", "Dimension2(0mm,0mm)")
        box1.transform.scale.expression = "Vector2(0.25, 0.5)"
        // Box1 is really 30mm,120 at 0,0

        val context = MockContext()
        val view = PageView(page, context)

        view.draw()

        assertEquals("""
            |M-15,-60
            |L15,-60
            |L15,60
            |L-15,60
            |L-15,-60


        """.trimMargin(), context.toString())
    }

    @Test

    fun testBoxInScaledBox() {

        val box1 = createBox(page, "Dimension2(120mm,240mm)", "Dimension2(0mm,0mm)")
        createBox(box1, "Dimension2(24mm,40mm)", "Parent.LocPin") // At the center of box1
        box1.transform.scale.expression = "Vector2(0.25, 0.5)"
        // Box 1 is really 30x120 at 0,0
        // Box 2 is really 30x20 at 0,0

        val context = MockContext()
        val view = PageView(page, context)

        view.draw()

        assertEquals("""
            |M-15,-60
            |L15,-60
            |L15,60
            |L-15,60
            |L-15,-60

            |M-3,-10
            |L3,-10
            |L3,10
            |L-3,10
            |L-3,-10


            """.trimMargin(), context.buffer.toString())
    }

    fun testTranslatedBoxInScaledBox() {
        val box1 = createBox(page, "Dimension2(120mm,240mm)", "Dimension2(0mm,0mm)")
        createBox(box1, "Dimension2(24mm,40mm)", "Parent.LocPin + Dimension2(4mm,10mm)")
        box1.transform.scale.expression = "Vector2(0.25, 0.5)"
        // Box 1 is really 30x120 at 0,0
        // Box 2 is really 30x20 at 1,5

        val context = MockContext()
        val view = PageView(page, context)

        view.draw()

        assertEquals("""
            |M-15,-60
            |L15,-60
            |L15,60
            |L-15,60
            |L-15,-60

            |M-2,-5
            |L4,-5
            |L4,15
            |L-2,15
            |L-2,-5


        """.trimMargin(), context.toString())
    }

    @Test
    fun testScaledBoxInBox() {
        val box1 = createBox(page, "Dimension2(120mm,240mm)", "Dimension2(0mm,0mm)")
        val box2 = createBox(box1, "Dimension2(24mm,40mm)", "Parent.LocPin")
        box2.transform.scale.expression = "Vector2(3,2)"
        // Box 1 is 120x240 at 0,0
        // Box 2 is 72x80 at 0,0
        val context = MockContext()
        val view = PageView(page, context)

        view.draw()

        assertEquals("""
            |M-60,-120
            |L60,-120
            |L60,120
            |L-60,120
            |L-60,-120

            |M-36,-40
            |L36,-40
            |L36,40
            |L-36,40
            |L-36,-40


        """.trimMargin(), context.toString())

    }

}
