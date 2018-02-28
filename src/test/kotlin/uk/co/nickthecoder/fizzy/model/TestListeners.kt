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
import uk.co.nickthecoder.fizzy.util.ChangeListener
import uk.co.nickthecoder.fizzy.util.ChangeType
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestListeners : MyTestCase() {

    val document = Document()

    val layer1 = Page(document)
    val layer2 = Page(document)

    val shape1a = Shape2d.create(layer1)
    val shape1b = Shape1d.create(layer1)

    var pageChanged = 0
    var layer1Changed = 0
    var layer2Changed = 0
    var shape1aChanged = 0
    var shape1bChanged = 0

    /**
     * This is a quick and dirty way of keeping a reference to the listeners, so that they don't get gc'd
     */
    private val changeListeners = mutableListOf<Any>()

    fun <T : Any> changeListener(action: () -> Unit): ChangeListener<T> {

        val listener = object : ChangeListener<T> {
            override fun changed(item: T, changeType: ChangeType, obj: Any?) {
                action()
            }
        }
        changeListeners.add(listener)
        return listener
    }

    override fun setUp() {
        super.setUp()
        pageChanged = 0
        layer1Changed = 0
        layer2Changed = 0
        shape1aChanged = 0
        shape1bChanged = 0

        document.changeListeners.add(changeListener { pageChanged++ })
        layer1.changeListeners.add(changeListener { layer1Changed++ })
        layer2.changeListeners.add(changeListener { layer2Changed++ })
        shape1a.changeListeners.add(changeListener { shape1aChanged++ })
        shape1b.changeListeners.add(changeListener { shape1bChanged++ })
    }

    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testSize() {
        shape1a.size.value // Ensure not dirty - without this, no change would be fired.
        shape1a.size.expression = "Dimension2(1mm,1mm)"
        assertEquals(1, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(0, layer2Changed)
        assertEquals(1, pageChanged)

        shape1a.size.value // Ensure not dirty
        assertEquals(1, shape1aChanged) // Make sure the line above doesn't fire a change.

        // Change it for a 2nd time.
        shape1a.size.expression = "Dimension2(1m,1m)"
        assertEquals(2, shape1aChanged)
        assertEquals(2, layer1Changed)
        assertEquals(2, pageChanged)

        // Change it for a 3rd time.
        // But as it is already dirty, no additional change events should be fired.
        shape1a.size.expression = "Dimension2(1cm,1cm)"
        assertEquals(2, shape1aChanged)
        assertEquals(2, layer1Changed)
        assertEquals(2, pageChanged)

    }

    @Test
    fun testPosition() {
        shape1a.transform.pin.value // Ensure not dirty
        shape1a.transform.pin.expression = "Dimension2(1mm,1mm)"
        assertEquals(1, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(0, layer2Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testLocalPosition() {
        shape1a.transform.locPin.value // Ensure not dirty
        shape1a.transform.locPin.expression = "Dimension2(1mm,1mm)"
        assertEquals(1, shape1aChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testScale() {
        shape1a.transform.scale.value // Ensure not dirty
        shape1a.transform.scale.expression = "Vector2(1,)"
        assertEquals(1, shape1aChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testRotation() {
        shape1a.transform.rotation.value // Ensure not dirty
        shape1a.transform.rotation.expression = "30 deg"
        assertEquals(1, shape1aChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testStart() {
        shape1b.start.value // Ensure not dirty
        shape1b.start.expression = "Dimension2(3m,2m)"
        assertEquals(1, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testEnd() {
        shape1b.start.value // Ensure not dirty
        shape1b.start.expression = "Dimension2(3m,2m)"
        assertEquals(1, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testId() {
        shape1a.name.value // Ensure not dirty
        shape1b.name.value // Ensure not dirty

        shape1a.name.expression = "Shape1A"
        assertEquals(1, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)

        shape1b.name.expression = "Shape1B"
        assertEquals(1, shape1aChanged)
        assertEquals(1, shape1bChanged)
        assertEquals(2, layer1Changed)
        assertEquals(2, pageChanged)
    }

    @Test
    fun testGeometry() {
        val geometry = Geometry()
        shape1a.geometries.add(geometry)
        assertEquals(1, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)

        val mt = MoveTo()
        geometry.parts.add(mt)
        assertEquals(2, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(2, layer1Changed)
        assertEquals(2, pageChanged)

        val lt = LineTo()
        geometry.parts.add(lt)
        assertEquals(3, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(3, layer1Changed)
        assertEquals(3, pageChanged)

    }
}
