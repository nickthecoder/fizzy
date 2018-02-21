package uk.co.nickthecoder.fizzy.model

import junit.framework.TestCase
import org.junit.Test

class TestListeners : TestCase() {

    val page = Page()

    val layer1 = Layer(page)
    val layer2 = Layer(page)

    val shape1a = Shape2d(layer1)
    val shape1b = Shape2d(layer1)

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

        page.listeners.add(changeListener { pageChanged++ })
        layer1.listeners.add(changeListener { layer1Changed++ })
        layer2.listeners.add(changeListener { layer2Changed++ })
        shape1a.listeners.add(changeListener { shape1aChanged++ })
        shape1b.listeners.add(changeListener { shape1bChanged++ })
    }

    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testSize() {
        shape1a.size.value // Ensure not dirty
        shape1a.size.expression = "Dimension2(1mm,1mm)"
        shape1a.size.value // Ensure not dirty
        assertEquals(1, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(0, layer2Changed)
        assertEquals(1, pageChanged)
        shape1a.size.expression = "Dimension2(1m,1m)"
        assertEquals(2, shape1aChanged)
        assertEquals(2, layer1Changed)
        assertEquals(2, pageChanged)
        shape1a.size.expression = "Dimension2(1cm,1cm)"
        // As we haven't caused the expression to be re-evaluated, no more change events should be fired.
        assertEquals(2, shape1aChanged)
        assertEquals(2, layer1Changed)
        assertEquals(2, pageChanged)

    }

    @Test
    fun testPosition() {
        shape1a.position.value // Ensure not dirty
        shape1a.position.expression = "Dimension2(1mm,1mm)"
        shape1a.position.value // Ensure not dirty
        assertEquals(1, shape1aChanged)
        assertEquals(0, shape1bChanged)
        assertEquals(1, layer1Changed)
        assertEquals(0, layer2Changed)
        assertEquals(1, pageChanged)
    }

    @Test
    fun testLocalPosition() {
        shape1a.localPosition.value // Ensure not dirty
        shape1a.localPosition.expression = "Dimension2(1mm,1mm)"
        shape1a.localPosition.value // Ensure not dirty
        assertEquals(1, shape1aChanged)
        assertEquals(1, layer1Changed)
        assertEquals(1, pageChanged)
    }
}
