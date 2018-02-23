package uk.co.nickthecoder.fizzy.evaluator

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.prop.AngleExpression
import uk.co.nickthecoder.fizzy.prop.DoubleExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestAngle : MyTestCase() {

    @Test
    fun testAngles() {
        assertEquals(2.0, Angle.radians(2.0).radians, tiny)
        assertEquals(2.0 / Math.PI * 180.0, Angle.radians(2.0).degrees, tiny)

        assertEquals(45.0, Angle.degrees(45.0).degrees, tiny)
        assertEquals(45.0, Angle.radians(45.0 / 180.0 * Math.PI).degrees, tiny)

        val a = Evaluator("0 deg").parse() as Prop<Angle>
        assertEquals(0.0, a.value.radians, tiny)
        assertEquals(0.0, a.value.degrees, tiny)

        val a2 = Evaluator("0 rad").parse() as Prop<Angle>
        assertEquals(0.0, a2.value.radians, tiny)
        assertEquals(0.0, a2.value.degrees, tiny)

        val b = Evaluator("45 deg").parse() as Prop<Angle>
        assertEquals(45.0, b.value.degrees, tiny)
        assertEquals(45.0 / 180.0 * Math.PI, b.value.radians, tiny)

        val b2 = Evaluator("2 rad").parse() as Prop<Angle>
        assertEquals(2.0, b2.value.radians, tiny)
        assertEquals(2.0 * 180.0 / Math.PI, b2.value.degrees, tiny)

        val c = Evaluator("30 deg + 20 deg").parse() as Prop<Angle>
        assertEquals(50.0, c.value.degrees, tiny)

        val d = Evaluator("30 deg * 3").parse() as Prop<Angle>
        assertEquals(90.0, d.value.degrees, tiny)

        val e = Evaluator("90 deg /2").parse() as Prop<Angle>
        assertEquals(45.0, e.value.degrees, tiny)

        assertFailsAt(2) {
            Evaluator("5 / 2deg").parse()
        }

        val f = Evaluator("-90 deg").parse() as Prop<Angle>
        assertEquals(-90.0, f.value.degrees, tiny)

        val g = Evaluator("-(80 deg)").parse() as Prop<Angle>
        assertEquals(-80.0, g.value.degrees, tiny)

        val h = Evaluator("80 deg / 20 deg").parse() as Prop<Double>
        assertEquals(4.0, h.value, tiny)

        assertFailsAt(7) {
            Evaluator("10 deg * 2deg").parse()
        }
    }


    @Test
    fun testDynamicAngles() {

        val variables = SimpleEvaluationContext()
        val context = CompoundEvaluationContext(listOf(constantsContext, variables))

        variables.putProp("value1", DoubleExpression("80", context))
        variables.putProp("angle1", AngleExpression("value1 deg", context))
        variables.putProp("angle2", AngleExpression("PI", context))

        assertEquals(Angle.radians(Math.PI), variables.findProp("angle2")?.value)
        assertEquals(Angle.degrees(80.0), variables.findProp("angle1")?.value)

        val a = Evaluator("angle2 - angle1", context).parse() as Prop<Angle>
        val value1 = variables.findProp("value1") as PropExpression<Double>

        assertEquals(100.0, a.value.degrees, tiny)
        value1.expression = "100"
        assertEquals(80.0, a.value.degrees, tiny)


        val b = Evaluator("angle1.degrees", context).parse() as Prop<Double>
        assertEquals(100.0, b.value, tiny)
        value1.expression = "80"
        assertEquals(80.0, b.value, tiny)
    }

    @Test
    fun testAngleFields() {
        val a = Evaluator("90 deg.degrees").parse() as Prop<Double>
        assertEquals(90.0, a.value, tiny)

        val b = Evaluator("PI.degrees").parse() as Prop<Double>
        assertEquals(180.0, b.value, tiny)

        val c = Evaluator("PI.radians").parse() as Prop<Double>
        assertEquals(Math.PI, c.value, tiny)
    }


    @Test
    fun testTrig() {

        val a1 = Evaluator("Vector2(2, 2).angle()").parse() as Prop<Angle>
        assertEquals(45.0, a1.value.degrees, tiny)

        val a2 = Evaluator("Vector2(-2,2).angle()").parse() as Prop<Angle>
        assertEquals(135.0, a2.value.degrees, tiny)

        val a3 = Evaluator("Vector2(2,-2).angle()").parse() as Prop<Angle>
        assertEquals(-45.0, a3.value.degrees, tiny)

        val a4 = Evaluator("Vector2(-2,-2).angle()").parse() as Prop<Angle>
        assertEquals(-135.0, a4.value.degrees, tiny)

        val a5 = Evaluator("Vector2(2,0).angle()").parse() as Prop<Angle>
        assertEquals(0.0, a5.value.degrees, tiny)

        for (angle in -170..170 step 20) {
            val b1 = Evaluator("$angle deg.sin()").parse() as Prop<Double>
            assertEquals(Math.sin(Math.toRadians(angle.toDouble())), b1.value, tiny)

            val b2 = Evaluator("$angle deg.cos()").parse() as Prop<Double>
            assertEquals(Math.cos(Math.toRadians(angle.toDouble())), b2.value, tiny)

            val b3 = Evaluator("$angle deg.tan()").parse() as Prop<Double>
            assertEquals(Math.tan(Math.toRadians(angle.toDouble())), b3.value, tiny)

            // Tests Vector2.rotate method (but not well,  because y is always 0)
            val b4 = Evaluator("Vector2(1.3,0).rotate($angle deg).angle()").parse() as Prop<Angle>
            assertEquals(angle.toDouble(), b4.value.degrees, tiny)

            // Tests Vector2.rotate method
            val b5 = Evaluator("Vector2(1.3,0).rotate(4 deg).rotate($angle deg).angle()").parse() as Prop<Angle>
            assertEquals(angle.toDouble() + 4, b5.value.degrees, tiny)

            // Tests Dimension2.rotate method
            val b6 = Evaluator("Dimension2(1.3m,0m).rotate(4 deg).rotate($angle deg).angle()").parse() as Prop<Angle>
            assertEquals(angle.toDouble() + 4, b6.value.degrees, tiny)

        }

        val c = Evaluator("Dimension2(1m,50cm).rotate(20 deg)").parse() as Prop<Dimension2>
        assertEquals(1.0, c.value.x.power, tiny)
        assertEquals(1.0, c.value.y.power, tiny)
        assertEquals(Dimension.Units.m, c.value.x.units)
        assertEquals(Dimension.Units.m, c.value.y.units)
    }

}
