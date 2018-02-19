package uk.co.nickthecoder.fizzy.evaluator

import junit.framework.TestCase
import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.*
import java.lang.RuntimeException

@Suppress("UNCHECKED_CAST")
class TestEvaluator : TestCase() {

    val tiny = 0.000001

    fun assertFailsAt(position: Int, expression: () -> Any) {
        try {
            expression()
            throw RuntimeException("Expected an EvaluationException")
        } catch (e: EvaluationException) {
            if (position != e.index) {
                throw RuntimeException("Expected an EvaluationException at $position, but found one at ${e.index}", e)
            }
        }
    }

    @Test
    fun testConstant() {
        val one = Evaluator("1").parse() as Prop<Double>
        assertEquals(1.0, one.value, tiny)

        val two = Evaluator("  2  ").parse() as Prop<Double>
        assertEquals(2.0, two.value, tiny)

        val oneAndAHalf = Evaluator("1.5").parse() as Prop<Double>
        assertEquals(1.5, oneAndAHalf.value, tiny)
    }

    @Test
    fun testSimpleMaths() {
        val onePlusOne = Evaluator("1 + 1").parse() as Prop<Double>
        assertEquals(2.0, onePlusOne.value, tiny)

        val oneMinusTwo = Evaluator("1 - 2").parse() as Prop<Double>
        assertEquals(-1.0, oneMinusTwo.value, tiny)

        val twoTimes3 = Evaluator("2 * 3").parse() as Prop<Double>
        assertEquals(6.0, twoTimes3.value, tiny)

        val threeDivTwo = Evaluator("3 / 2").parse() as Prop<Double>
        assertEquals(1.5, threeDivTwo.value, tiny)
    }

    @Test
    fun testUnaryMinus() {
        val a = Evaluator("3 + -2").parse() as Prop<Double>
        assertEquals(1.0, a.value, tiny)

        val b = Evaluator("3 * -2").parse() as Prop<Double>
        assertEquals(-6.0, b.value, tiny)
    }

    @Test
    fun testPrecedence() {
        val pluses = Evaluator("1 + 2 + 3").parse() as Prop<Double>
        assertEquals(6.0, pluses.value, tiny)

        val timesPlus = Evaluator("2 * 3 + 4").parse() as Prop<Double>
        assertEquals(10.0, timesPlus.value, tiny)

        val plusTimes = Evaluator("1 + 2 * 3").parse() as Prop<Double>
        assertEquals(7.0, plusTimes.value, tiny)

        val divMinus = Evaluator("4 / 2 - 1").parse() as Prop<Double>
        assertEquals(1.0, divMinus.value, tiny)

        val minusDiv = Evaluator("8 - 6 / 3").parse() as Prop<Double>
        assertEquals(6.0, minusDiv.value, tiny)
    }

    @Test
    fun testBrackets() {
        val pluses1 = Evaluator("(1 + 2) + 3").parse() as Prop<Double>
        assertEquals(6.0, pluses1.value, tiny)

        val pluses2 = Evaluator("1 + (2 + 3)").parse() as Prop<Double>
        assertEquals(6.0, pluses2.value, tiny)

        val timesPlus1 = Evaluator("(2 * 3) + 4").parse() as Prop<Double>
        assertEquals(10.0, timesPlus1.value, tiny)

        val timesPlus2 = Evaluator("2 * (3 + 4)").parse() as Prop<Double>
        assertEquals(14.0, timesPlus2.value, tiny)

        val plusTimes1 = Evaluator("(1 + 2) * 3").parse() as Prop<Double>
        assertEquals(9.0, plusTimes1.value, tiny)

        val plusTimes2 = Evaluator("1 + (2 * 3)").parse() as Prop<Double>
        assertEquals(7.0, plusTimes2.value, tiny)

        val divMinus1 = Evaluator("(4 / 2) - 1").parse() as Prop<Double>
        assertEquals(1.0, divMinus1.value, tiny)

        val divMinus2 = Evaluator("4 / (2 - 1)").parse() as Prop<Double>
        assertEquals(4.0, divMinus2.value, tiny)

        val minusDiv1 = Evaluator("(8 - 6) / 3").parse() as Prop<Double>
        assertEquals(2.0 / 3.0, minusDiv1.value, tiny)

        val minusDiv2 = Evaluator("8 - (6 / 3)").parse() as Prop<Double>
        assertEquals(6.0, minusDiv2.value, tiny)
    }

    @Test
    fun testStrings() {

        val empty = Evaluator("\"\"").parse() as Prop<String>
        assertEquals("", empty.value)

        val simple = Evaluator("\"Hello\"").parse() as Prop<String>
        assertEquals("Hello", simple.value)

        val withSpaces = Evaluator("\"Hello World\"").parse() as Prop<String>
        assertEquals("Hello World", withSpaces.value)

        val plus = Evaluator("\"Hello\" + \"World\"").parse() as Prop<String>
        assertEquals("HelloWorld", plus.value)

        val literalQuote = Evaluator("\"Hello\\\"World\"").parse() as Prop<String>
        assertEquals("Hello\"World", literalQuote.value)

        val literalNewLine = Evaluator("\"Hello\\nWorld\"").parse() as Prop<String>
        assertEquals("Hello\nWorld", literalNewLine.value)

        val literalTab = Evaluator("\"Hello\\tWorld\"").parse() as Prop<String>
        assertEquals("Hello\tWorld", literalTab.value)

        val literalSlash = Evaluator("\"Hello\\\\World").parse() as Prop<String>
        assertEquals("Hello\\World", literalSlash.value)

        val literalSlashAtEnd = Evaluator("\"Hello\\\\").parse() as Prop<String>
        assertEquals("Hello\\", literalSlashAtEnd.value)

        val plusEmpty = Evaluator("\"Hello\" + \"\"").parse() as Prop<String>
        assertEquals("Hello", plusEmpty.value)
    }

    @Test
    fun testInvalidOperands() {
        assertFailsAt(8) {
            Evaluator("\"Hello\" + 3").parse()
        }

        assertFailsAt(2) {
            Evaluator("3 + \"Hello\"").parse()
        }

        assertFailsAt(4) {
            Evaluator("3 + * 2").parse()
        }
    }

    @Test
    fun testConstantIdentifiers() {
        val pi = Evaluator("PI").parse() as Prop<Angle>
        assertEquals(Math.PI, pi.value.radians, tiny)

        val tauMinus2Pi = Evaluator("TAU - 2 * PI").parse() as Prop<Angle>
        assertEquals(0.0, tauMinus2Pi.value.radians, tiny)

        val e = Evaluator("E").parse() as Prop<Double>
        assertEquals(Math.E, e.value, tiny)
    }

    @Test
    fun testFunctions() {
        val sqrt = Evaluator("sqrt(4)").parse() as Prop<Double>
        assertEquals(2.0, sqrt.value, tiny)
    }

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
    fun testDimensions() {
        val a = Evaluator("10mm").parse() as Prop<Dimension>
        assertEquals(10.0, a.value.mm, tiny)
        assertEquals(1.0, a.value.cm, tiny)
        assertEquals(0.01, a.value.m, tiny)
        assertEquals(0.00001, a.value.km, tiny)

        val b = Evaluator("1mm + 2cm").parse() as Prop<Dimension>
        assertEquals(2.1, b.value.cm, tiny)

        val c = Evaluator("2 cm - 1 mm").parse() as Prop<Dimension>
        assertEquals(1.9, c.value.cm, tiny)

        val d = Evaluator("2 cm * 4").parse() as Prop<Dimension>
        assertEquals(8.0, d.value.cm, tiny)

        val e = Evaluator("2 cm / 4").parse() as Prop<Dimension>
        assertEquals(0.5, e.value.cm, tiny)

        assertFailsAt(2) {
            Evaluator("1 / 1cm").parse()
        }
        assertFailsAt(2) {
            Evaluator("1 + 1cm").parse()
        }
        assertFailsAt(4) {
            Evaluator("1cm + 1").parse()
        }
        assertFailsAt(4) {
            Evaluator("1cm - 1").parse()
        }
        assertFailsAt(2) {
            Evaluator("1 - 1cm").parse()
        }

        val f = Evaluator("2mm * 8mm").parse() as Prop<Dimension>
        assertEquals(16.0, f.value.mm, tiny)
        assertEquals(2.0, f.value.power)

        val g = Evaluator("sqrt(2mm * 8mm)").parse() as Prop<Dimension>
        assertEquals(4.0, g.value.mm, tiny)
        assertEquals(1.0, g.value.power)

        val h = Evaluator("ratio(10mm, 2mm)").parse() as Prop<Double>
        assertEquals(5.0, h.value, tiny)

        val i = Evaluator("ratio(20cm * 20cm, 200cm * 4cm)").parse() as Prop<Double>
        assertEquals(0.5, i.value, tiny)

        val j = Evaluator("ratio(0.2m * 0.2m, 200cm * 4cm)").parse() as Prop<Double>
        assertEquals(0.5, j.value, tiny)

    }

    @Test
    fun testCreateVector() {

        val a = Evaluator("Vector2(1,2)").parse() as Prop<Vector2>
        assertEquals(1.0, a.value.x, tiny)
        assertEquals(2.0, a.value.y, tiny)
    }

    @Test
    fun testVectorMaths() {
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
    fun testCreateDimension2() {

        val a = Evaluator("Dimension2(1 mm, 2 mm)").parse() as Prop<Dimension2>
        assertEquals(1.0, a.value.x.mm, tiny)
        assertEquals(2.0, a.value.y.mm, tiny)
    }

    @Test
    fun testVectorDimension2() {

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

        val d = Evaluator("Dimension2(1mm,2mm) + Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(9.0, d.value.x.mm, tiny)
        assertEquals(12.0, d.value.y.mm, tiny)

        val e = Evaluator("Dimension2(1mm,2mm) - Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(-7.0, e.value.x.mm, tiny)
        assertEquals(-8.0, e.value.y.mm, tiny)

        val f = Evaluator("Dimension2(3mm,2mm) * Dimension2(8mm,10mm)").parse() as Prop<Dimension2>
        assertEquals(24.0, f.value.x.mm, tiny)
        assertEquals(20.0, f.value.y.mm, tiny)

        val g = Evaluator("-Dimension2(3mm,2mm)").parse() as Prop<Dimension2>
        assertEquals(-3.0, g.value.x.mm, tiny)
        assertEquals(-2.0, g.value.y.mm, tiny)

    }

    @Test
    fun testVectorWithDimension() {
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
    fun testCoercion() {
        // Non coercion example (always a Double)
        val a = DoubleExpression("4")
        assertEquals(4.0, a.value, tiny)

        // No attempt to coerce
        val b = DimensionExpression("6mm / 2mm")
        assertEquals(3.0, b.value.mm, tiny)
        assertEquals(0.0, b.value.power, tiny)

        // coerce from a Dimension to a Double
        val c = DoubleExpression("6mm / 2mm")
        assertEquals(3.0, c.value, tiny)

        // Non coercion example (always a Vector)
        val d = Vector2Expression("Vector2(2,3)")
        assertEquals(2.0, d.value.x, tiny)
        assertEquals(3.0, d.value.y, tiny)

        // Coerce from a Dimension2 to a Vector2
        val e = Vector2Expression("Dimension2(10mm,8mm) / Dimension2(5mm,2mm)")
        assertEquals(2.0, e.value.x, tiny)
        assertEquals(4.0, e.value.y, tiny)

        // No attempt to coerce
        val f = Dimension2Expression("Dimension2(10mm,8mm) / Dimension2(5mm,2mm)")
        assertEquals(2.0, f.value.x.mm, tiny)
        assertEquals(4.0, f.value.y.mm, tiny)
        assertEquals(0.0, f.value.x.power, tiny)
        assertEquals(0.0, f.value.y.power, tiny)
    }

    @Test
    fun testDynamicDimensions() {

        val variables = SimpleContext()
        val context = CompoundContext(listOf(constantsContext, variables))

        variables.putProp("x", DoubleExpression("3", context))
        variables.putProp("y", DoubleExpression("4", context))

        variables.putProp("width", DimensionExpression("x mm", context))
        variables.putProp("height", DimensionExpression("y cm", context))

        val a = Evaluator("width + height", context).parse() as Prop<Dimension>
        val x = variables.findProp("x") as PropExpression<Double>

        assertEquals(43.0, a.value.mm, tiny)
        x.expression = "5"
        assertEquals(45.0, a.value.mm, tiny)
    }

    @Test
    fun testDynamicAngles() {

        val variables = SimpleContext()
        val context = CompoundContext(listOf(constantsContext, variables))

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
    fun testDimensionFields() {
        val a = Evaluator("15mm.mm").parse() as Prop<Double>
        assertEquals(15.0, a.value, tiny)

        val b = Evaluator("15mm.cm").parse() as Prop<Double>
        assertEquals(1.5, b.value, tiny)

        val c = Evaluator("15mm.m").parse() as Prop<Double>
        assertEquals(0.015, c.value, tiny)

        val d = Evaluator("15mm.km").parse() as Prop<Double>
        assertEquals(0.000015, d.value, tiny)
    }

    @Test
    fun testDimension2Fields() {
        val a = Evaluator("Dimension2(15mm,10cm).x").parse() as Prop<Dimension>
        assertEquals(15.0, a.value.mm, tiny)

        val b = Evaluator("Dimension2(15mm,10cm).y").parse() as Prop<Dimension>
        assertEquals(100.0, b.value.mm, tiny)

        val c = Evaluator("Dimension2(15mm,10cm).y.mm").parse() as Prop<Double>
        assertEquals(100.0, c.value, tiny)
    }

    @Test
    fun testVector2Fields() {
        val a = Evaluator("Vector2(15,10).x").parse() as Prop<Double>
        assertEquals(15.0, a.value, tiny)

        val b = Evaluator("Vector2(15,10).y").parse() as Prop<Double>
        assertEquals(10.0, b.value, tiny)
    }

    @Test
    fun testString2Fields() {
        val a = Evaluator("\"Hello\".length").parse() as Prop<Double>
        assertEquals(5.0, a.value, tiny)
    }

    //variables.putProp("angle1", ExpressionProp("value1 degrees", Angle::class, context))

}
