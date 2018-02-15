package uk.co.nickthecoder.fizzy.prop

import junit.framework.TestCase
import org.junit.Test

class TestDoubleProp : TestCase() {

    val tiny = 0.000001

    val zero = DoubleValue(0.0)
    val unit = DoubleValue(1.0)

    val a = DoubleValue(2.5)
    val b = DoubleValue(0.5)

    @Test
    fun testValues() {
        assertEquals(0.0, zero.value, tiny)
        assertEquals(1.0, unit.value, tiny)
        assertEquals(2.5, a.value, tiny)
        assertEquals(0.5, b.value, tiny)
    }

    fun testChainedPlus() {
        val x = DoubleValue(1.0)
        val y = DoubleValue(0.5)
        val z = DoubleValue(0.25)

        val total = DoublePlus(x, DoublePlus(y, z))
        assertEquals(1.75, total.value, tiny)

        x.value = 2.0
        assertEquals(2.75, total.value, tiny)

        y.value = 0.6
        assertEquals(2.85, total.value, tiny)

        z.value = 0.2
        assertEquals(2.8, total.value, tiny)
    }

    @Test
    fun testChainedMinus() {
        val x = DoubleValue(1.0)
        val y = DoubleValue(0.5)
        val z = DoubleValue(0.25)

        val total = DoubleMinus(DoubleMinus(x, y), z)
        assertEquals(0.25, total.value, tiny)

        x.value = 2.0
        assertEquals(1.25, total.value, tiny)

        y.value = 0.6
        assertEquals(1.15, total.value, tiny)

        z.value = 0.2
        assertEquals(1.2, total.value, tiny)
    }

}