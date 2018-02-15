package uk.co.nickthecoder.fizzy.prop

import junit.framework.TestCase
import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Vector2

class TestVector2 : TestCase() {

    val tiny = 0.000001

    val zero = Vector2(0.0, 0.0)
    val unit = Vector2(1.0, 1.0)

    val a = Vector2(2.0, 1.0)
    val b = Vector2(0.5, 3.5)

    override fun tearDown() {
        // Make sure values haven't changed
        assertEquals(0.0, zero.x, tiny)
        assertEquals(0.0, zero.y, tiny)

        assertEquals(1.0, unit.y, tiny)
        assertEquals(1.0, unit.x, tiny)

        assertEquals(2.0, a.x, tiny)
        assertEquals(1.0, a.y, tiny)

        assertEquals(0.5, b.x, tiny)
        assertEquals(3.5, b.y, tiny)
    }

    @Test
    fun testValues() {
        assertEquals(0.0, zero.x, tiny)
        assertEquals(0.0, zero.y, tiny)

        assertEquals(1.0, unit.x, tiny)
        assertEquals(1.0, unit.y, tiny)

        assertEquals(2.0, a.x, tiny)
        assertEquals(1.0, a.y, tiny)
    }

    @Test
    fun testPlus() {

        // unit + zero
        val d = unit + zero
        assertEquals(1.0, d.x, tiny)
        assertEquals(1.0, d.y, tiny)

        // a + b
        val f = a + b
        assertEquals(2.5, f.x, tiny)
        assertEquals(4.5, f.y, tiny)

    }

    @Test
    fun testMinus() {
        // unit - zero
        val d = unit + zero
        assertEquals(1.0, d.x, tiny)
        assertEquals(1.0, d.y, tiny)

        // a - b
        val f = a - b
        assertEquals(1.5, f.x, tiny)
        assertEquals(-2.5, f.y, tiny)
    }

}
