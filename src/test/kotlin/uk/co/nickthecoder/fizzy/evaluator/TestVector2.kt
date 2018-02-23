package uk.co.nickthecoder.fizzy.evaluator

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestVector2 : MyTestCase() {

    @Test
    fun testCreate() {

        val a = Evaluator("Vector2(1,2)").parse() as Prop<Vector2>
        assertEquals(1.0, a.value.x, tiny)
        assertEquals(2.0, a.value.y, tiny)
    }

    @Test
    fun testMaths() {
        val a = Evaluator("Vector2(1,2) * 2").parse() as Prop<Vector2>
        assertEquals(2.0, a.value.x, tiny)
        assertEquals(4.0, a.value.y, tiny)

        val b = Evaluator("Vector2(8,10) / 2").parse() as Prop<Vector2>
        assertEquals(4.0, b.value.x, tiny)
        assertEquals(5.0, b.value.y, tiny)

        val c = Evaluator("10 * Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(80.0, c.value.x, tiny)
        assertEquals(100.0, c.value.y, tiny)

        assertFailsAt(2) {
            Evaluator("5 / Vector2(1,1)").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 + Vector2(1,1)").parse()
        }
        assertFailsAt(13) {
            Evaluator("Vector2(1,1) + 5").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 - Vector2(1,1)").parse()
        }
        assertFailsAt(13) {
            Evaluator("Vector2(1,1) - 5").parse()
        }

        val d = Evaluator("Vector2(1,2) + Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(9.0, d.value.x, tiny)
        assertEquals(12.0, d.value.y, tiny)

        val e = Evaluator("Vector2(1,2) - Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(-7.0, e.value.x, tiny)
        assertEquals(-8.0, e.value.y, tiny)

        val f = Evaluator("Vector2(3,2) * Vector2(8,10)").parse() as Prop<Vector2>
        assertEquals(24.0, f.value.x, tiny)
        assertEquals(20.0, f.value.y, tiny)

        val g = Evaluator("-Vector2(3,2)").parse() as Prop<Vector2>
        assertEquals(-3.0, g.value.x, tiny)
        assertEquals(-2.0, g.value.y, tiny)

    }

    @Test
    fun testIsConstant() {

        //val b = Evaluator("Vector2(1,2)").parse()
        // FAILS : assert(b is PropConstant<*>) { "is ${a.javaClass}" }
        val a2 = Evaluator("Vector2(1 + 1,2)").parse()
        assert(a2 !is PropConstant<*>) { "is ${a2.javaClass}" }
        val a3 = Evaluator("Vector2(1,2+1)").parse()
        assert(a3 !is PropConstant<*>) { "is ${a3.javaClass}" }
    }

    @Test
    fun testFields() {
        val a = Evaluator("Vector2(15,10).x").parse() as Prop<Double>
        assertEquals(15.0, a.value, tiny)

        val b = Evaluator("Vector2(15,10).y").parse() as Prop<Double>
        assertEquals(10.0, b.value, tiny)
    }

    @Test
    fun testMethods() {
        val a = Evaluator("Vector2(3,4).length()").parse() as Prop<Double>
        assertEquals(5.0, a.value, tiny)

        val b = Evaluator("Vector2(3,4).normalise()").parse() as Prop<Vector2>
        assertEquals(3.0 / 5.0, b.value.x, tiny)
        assertEquals(4.0 / 5.0, b.value.y, tiny)
    }
}