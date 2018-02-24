package uk.co.nickthecoder.fizzy.model

import org.junit.Test
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestMatrix33 : MyTestCase() {

    @Test
    fun testTranslate() {
        val src = Vector2(1.0, 2.0)
        val t1 = Matrix33.translate(3.0, 5.0)
        val result = t1 * src
        assertEquals(4.0, result.x, tiny)
        assertEquals(7.0, result.y, tiny)
    }

    @Test
    fun testScale() {
        val src = Vector2(2.0, 3.0)
        val t1 = Matrix33.scale(4.0, 5.0)
        val result = t1 * src
        assertEquals(8.0, result.x, tiny)
        assertEquals(15.0, result.y, tiny)
    }

    @Test
    fun testRotate() {
        val src = Vector2(2.0, 3.0)
        val t1 = Matrix33.rotate(Angle.degrees(90.0))
        val r1 = t1 * src
        assertEquals(-3.0, r1.x, tiny)
        assertEquals(2.0, r1.y, tiny)

        val t2 = Matrix33.rotate(Angle.degrees(-90.0))
        val r2 = t2 * src
        assertEquals(3.0, r2.x, tiny)
        assertEquals(-2.0, r2.y, tiny)
    }

    @Test
    fun testCombinations() {
        val src = Vector2(2.0, 3.0)
        val r90 = Matrix33.rotate(Angle.degrees(90.0))
        val s4_5 = Matrix33.scale(4.0, 5.0) // Scale 4,5
        val t3_6 = Matrix33.translate(3.0, 6.0) // Translate 3,6

        // Note that the order of the multiplication should be read right to left.

        val a = s4_5 * t3_6 * src // translate(3,6) then scale(4,5)
        assertEquals(20.0, a.x, tiny)
        assertEquals(45.0, a.y, tiny)

        val b = t3_6 * s4_5 * src // scale(4,5) then translate(3,6)
        assertEquals(11.0, b.x, tiny)
        assertEquals(21.0, b.y, tiny)

        val c = r90 * s4_5 * src // scale(4,5) then rotate(90)
        assertEquals(-15.0, c.x, tiny)
        assertEquals(8.0, c.y, tiny)

        val d = s4_5 * r90 * src // rotate(90) then scale(4,5)
        assertEquals(-12.0, d.x, tiny)
        assertEquals(10.0, d.y, tiny)

        val e = t3_6 * r90 * src // rotate(90) then translate(3,6)
        assertEquals(0.0, e.x, tiny)
        assertEquals(8.0, e.y, tiny)

        val f = r90 * t3_6 * src // translate(3,6) then rotate(90)
        assertEquals(-9.0, f.x, tiny)
        assertEquals(5.0, f.y, tiny)
    }
}
