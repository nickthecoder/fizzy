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
package uk.co.nickthecoder.fizzy.model

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.history.ChangeExpression
import uk.co.nickthecoder.fizzy.model.history.ChangeExpressions
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestHistory : MyTestCase() {

    /**
     * Do, undo and redo a pair of [ChangeExpression]s
     */
    @Test
    fun testChangeExpression() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2( 10mm,10mm)", "Dimension2(0mm,0mm)")

        val history = doc.history
        history.beginBatch()

        history.makeChange(ChangeExpression(box.size, "Dimension2( 20mm,20mm)"))
        history.makeChange(ChangeExpression(box.size, "Dimension2( 30mm,30mm)"))
        // The above will be merged into one

        history.endBatch()
        assertEquals(30.0, box.size.value.x.mm)
        history.undo()
        assertEquals(10.0, box.size.value.x.mm)
        history.redo()
        assertEquals(30.0, box.size.value.x.mm)
    }

    /**
     * Do, undo and redo a pair of [ChangeExpressions]
     */
    @Test
    fun testChangeExpressions() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2( 10mm,10mm)", "Dimension2(0mm,0mm)")

        val history = doc.history
        history.beginBatch()

        history.makeChange(ChangeExpressions(listOf(box.size to "Dimension2( 20mm,20mm)")))
        history.makeChange(ChangeExpressions(listOf(box.size to "Dimension2( 30mm,30mm)")))
        // The above will be merged into one

        history.endBatch()
        assertEquals(30.0, box.size.value.x.mm)
        history.undo()
        assertEquals(10.0, box.size.value.x.mm)
        history.redo()
        assertEquals(30.0, box.size.value.x.mm)
    }


    @Test
    fun testMixedChangeExpression_s() {
        val doc = Document()
        val page = Page(doc)
        val box = createBox(page, "Dimension2( 10mm,10mm)", "Dimension2(0mm,0mm)")

        val history = doc.history
        history.beginBatch()

        // These won't be merged together, but should still undo/redo correctly.
        history.makeChange(ChangeExpression(box.size, "Dimension2( 10mm,10mm)"))
        history.makeChange(ChangeExpressions(listOf(box.size to "Dimension2( 30mm,30mm)")))

        history.endBatch()
        assertEquals(30.0, box.size.value.x.mm)

        history.beginBatch()
        // These won't be merged together, but should still undo/redo correctly.
        history.makeChange(ChangeExpressions(listOf(box.size to "Dimension2( 40mm,40mm)")))
        history.makeChange(ChangeExpression(box.size, "Dimension2( 20mm,20mm)"))
        history.endBatch()

        assertEquals(20.0, box.size.value.x.mm)
        assertTrue(history.canUndo())
        assertFalse(history.canRedo())

        history.undo()
        assertTrue(history.canUndo())
        assertTrue(history.canRedo())
        assertEquals(30.0, box.size.value.x.mm)

        history.undo()
        assertFalse(history.canUndo())
        assertTrue(history.canRedo())
        assertEquals(10.0, box.size.value.x.mm)
    }


}
