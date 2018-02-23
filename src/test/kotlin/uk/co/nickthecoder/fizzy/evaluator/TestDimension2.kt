package uk.co.nickthecoder.fizzy.evaluator

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestDimension2 : MyTestCase() {

    @Test
    fun testCreate() {

        val a = Evaluator("Dimension2(1 mm, 2 mm)").parse() as Prop<Dimension2>
        assertEquals(1.0, a.value.x.mm, tiny)
        assertEquals(2.0, a.value.y.mm, tiny)
    }

    @Test
    fun testIsConstant() {

        //val b = Evaluator("Dimension2(1m,2m)").parse()
        // FAILS : assert(b is PropConstant<*>) { "is ${a.javaClass}" }
        // FAILS : assertEquals(true, b.isConstant())

        val b2 = Evaluator("Dimension2((1+1)m,2m)").parse()
        assert(b2 !is PropConstant<*>) { "is ${b2.javaClass}" }
        assertEquals(false, b2.isConstant())
        val b3 = Evaluator("Dimension2(1m,(2+1)m)").parse()
        assert(b3 !is PropConstant<*>) { "is ${b3.javaClass}" }
        assertEquals(false, b3.isConstant())
    }

    @Test
    fun testMaths() {

        val a = Evaluator("Dimension2(1mm ,2mm) * 2").parse() as Prop<Dimension2>
        assertEquals(2.0, a.value.x.mm, tiny)
        assertEquals(4.0, a.value.y.mm, tiny)

        val b = Evaluator("Dimension2(8mm,10mm) / 2").parse() as Prop<Dimension2>
        assertEquals(4.0, b.value.x.mm, tiny)
        assertEquals(5.0, b.value.y.mm, tiny)

        assertFailsAt(14) {
            Evaluator("5 / Dimension2(1,1)").parse()
        }

        val c = Evaluator("10 * Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(80.0, c.value.x.mm, tiny)
        assertEquals(100.0, c.value.y.mm, tiny)

        val d = Evaluator("Dimension2(1mm,2mm) + Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(9.0, d.value.x.mm, tiny)
        assertEquals(12.0, d.value.y.mm, tiny)

        val e = Evaluator("Dimension2(1mm,2mm) - Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(-7.0, e.value.x.mm, tiny)
        assertEquals(-8.0, e.value.y.mm, tiny)

        val f = Evaluator("Dimension2(3mm,2mm) * Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(24.0, f.value.x.mm, tiny)
        assertEquals(20.0, f.value.y.mm, tiny)

        val g = Evaluator("-(Dimension2(3mm,2mm))").parse() as Prop<Dimension2>
        assertEquals(-3.0, g.value.x.mm, tiny)
        assertEquals(-2.0, g.value.y.mm, tiny)

        val h = Evaluator("-Dimension2(3mm,2mm)").parse() as Prop<Dimension2>
        assertEquals(-3.0, h.value.x.mm, tiny)
        assertEquals(-2.0, h.value.y.mm, tiny)

        val i = Evaluator("Dimension2(6mm,2mm).ratio( Dimension2(3mm, 2mm) )").parse() as Prop<Vector2>
        assertEquals(2.0, i.value.x, tiny)
        assertEquals(1.0, i.value.y, tiny)
        val i2 = Evaluator("Dimension2(6mm,2mm) % Dimension2(3mm, 2mm)").parse() as Prop<Vector2>
        assertEquals(2.0, i2.value.x, tiny)
        assertEquals(1.0, i2.value.y, tiny)
        val i3 = Evaluator("Dimension2(6m,2m) / Dimension2(3m, 2m)").parse() as Prop<Dimension2>
        assertEquals(2.0, i3.value.x.inDefaultUnits, tiny)
        assertEquals(1.0, i3.value.y.inDefaultUnits, tiny)
        assertEquals(0.0, i3.value.x.power, tiny)
        assertEquals(0.0, i3.value.y.power, tiny)

        val j = Evaluator("Dimension2(6m,2m) / 2").parse() as Prop<Dimension2>
        assertEquals(3.0, j.value.x.m, tiny)
        assertEquals(1.0, j.value.y.m, tiny)
    }

    @Test
    fun testIllegalOperations() {

        assertFailsAt(2) {
            Evaluator("5 / Dimension2(1mm,1mm)").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 + Dimension2(1mm,1mm)").parse()
        }
        assertFailsAt(20) {
            Evaluator("Dimension2(1mm,1mm) + 5").parse()
        }

        assertFailsAt(2) {
            Evaluator("5 - Dimension2(1mm,1mm)").parse()
        }
        assertFailsAt(20) {
            Evaluator("Dimension2(1mm,1mm) - 5").parse()
        }

    }

    @Test
    fun testWithVector2() {
        val a = Evaluator("Dimension2(3mm,2mm) * Vector2(2, 5)").parse() as Prop<Dimension2>
        assertEquals(6.0, a.value.x.mm, tiny)
        assertEquals(10.0, a.value.y.mm, tiny)

        val b = Evaluator("Vector2(5, 2) * Dimension2(3mm,2mm)").parse() as Prop<Dimension2>
        assertEquals(15.0, b.value.x.mm, tiny)
        assertEquals(4.0, b.value.y.mm, tiny)

        val c = Evaluator("Dimension2(6mm,3mm) / Vector2(2, 3)").parse() as Prop<Dimension2>
        assertEquals(3.0, c.value.x.mm, tiny)
        assertEquals(1.0, c.value.y.mm, tiny)

        assertFailsAt(13) {
            Evaluator("Vector2(1,1) + Dimension2(1m,1m)").parse()
        }
        assertFailsAt(13) {
            Evaluator("Vector2(1,1) - Dimension2(1m,1m)").parse()
        }
    }

    @Test
    fun testFields() {
        val a = Evaluator("Dimension2(15mm,10cm).x").parse() as Prop<Dimension>
        assertEquals(15.0, a.value.mm, tiny)

        val b = Evaluator("Dimension2(15mm,10cm).y").parse() as Prop<Dimension>
        assertEquals(100.0, b.value.mm, tiny)

        val c = Evaluator("Dimension2(15mm,10cm).y.mm").parse() as Prop<Double>
        assertEquals(100.0, c.value, tiny)
    }

    @Test
    fun testMethods() {
        val a = Evaluator("Dimension2(3m,4m).length()").parse() as Prop<Dimension>
        assertEquals(5.0, a.value.m, tiny)

        val b = Evaluator("Dimension2(3m,4m).normalise()").parse() as Prop<Vector2>
        assertEquals(3.0 / 5.0, b.value.x, tiny)
        assertEquals(4.0 / 5.0, b.value.y, tiny)
    }

}