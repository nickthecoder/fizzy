package uk.co.nickthecoder.fizzy.evaluator

import junit.framework.TestCase
import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Prop
import java.lang.RuntimeException

class TestEvaluator : TestCase() {

    val tiny = 0.000001

    @Test
    fun testConstant() {
        val one = Evaluator("1").parse()
        assertEquals(1.0, one.value as Double, tiny)

        val two = Evaluator("  2  ").parse()
        assertEquals(2.0, two.value as Double, tiny)

        val oneAndAHalf = Evaluator("1.5").parse()
        assertEquals(1.5, oneAndAHalf.value as Double, tiny)
    }

    @Test
    fun testSimpleMaths() {
        val onePlusOne = Evaluator("1 + 1").parse()
        assertEquals(2.0, onePlusOne.value as Double, tiny)

        val oneMinusTwo = Evaluator("1 - 2").parse()
        assertEquals(-1.0, oneMinusTwo.value as Double, tiny)

        val twoTimes3 = Evaluator("2 * 3").parse()
        assertEquals(6.0, twoTimes3.value as Double, tiny)

        val threeDivTwo = Evaluator("3 / 2").parse()
        assertEquals(1.5, threeDivTwo.value as Double, tiny)
    }

    @Test
    fun testUnaryMinus() {
        val a = Evaluator("3 + -2").parse()
        assertEquals(1.0, a.value as Double, tiny)

        val b = Evaluator("3 * -2").parse()
        assertEquals(-6.0, b.value as Double, tiny)
    }

    @Test
    fun testPrecedence() {
        val pluses = Evaluator("1 + 2 + 3").parse()
        assertEquals(6.0, pluses.value as Double, tiny)

        val timesPlus = Evaluator("2 * 3 + 4").parse()
        assertEquals(10.0, timesPlus.value as Double, tiny)

        val plusTimes = Evaluator("1 + 2 * 3").parse()
        assertEquals(7.0, plusTimes.value as Double, tiny)

        val divMinus = Evaluator("4 / 2 - 1").parse()
        assertEquals(1.0, divMinus.value as Double, tiny)

        val minusDiv = Evaluator("8 - 6 / 3").parse()
        assertEquals(6.0, minusDiv.value as Double, tiny)
    }

    @Test
    fun testBrackets() {
        val pluses1 = Evaluator("(1 + 2) + 3").parse()
        assertEquals(6.0, pluses1.value as Double, tiny)
        val pluses2 = Evaluator("1 + (2 + 3)").parse()
        assertEquals(6.0, pluses2.value as Double, tiny)

        val timesPlus1 = Evaluator("(2 * 3) + 4").parse()
        assertEquals(10.0, timesPlus1.value as Double, tiny)
        val timesPlus2 = Evaluator("2 * (3 + 4)").parse()
        assertEquals(14.0, timesPlus2.value as Double, tiny)

        val plusTimes1 = Evaluator("(1 + 2) * 3").parse()
        assertEquals(9.0, plusTimes1.value as Double, tiny)
        val plusTimes2 = Evaluator("1 + (2 * 3)").parse()
        assertEquals(7.0, plusTimes2.value as Double, tiny)

        val divMinus1 = Evaluator("(4 / 2) - 1").parse()
        assertEquals(1.0, divMinus1.value as Double, tiny)
        val divMinus2 = Evaluator("4 / (2 - 1)").parse()
        assertEquals(4.0, divMinus2.value as Double, tiny)

        val minusDiv1 = Evaluator("(8 - 6) / 3").parse()
        assertEquals(2.0 / 3.0, minusDiv1.value as Double, tiny)
        val minusDiv2 = Evaluator("8 - (6 / 3)").parse()
        assertEquals(6.0, minusDiv2.value as Double, tiny)
    }

    @Test
    fun testStrings() {

        val empty = Evaluator("\"\"").parse()
        assertEquals("", empty.value)

        val simple = Evaluator("\"Hello\"").parse()
        assertEquals("Hello", simple.value)

        val withSpaces = Evaluator("\"Hello World\"").parse()
        assertEquals("Hello World", withSpaces.value)

        val plus = Evaluator("\"Hello\" + \"World\"").parse()
        assertEquals("HelloWorld", plus.value)

        val literalQuote = Evaluator("\"Hello\\\"World\"").parse()
        assertEquals("Hello\"World", literalQuote.value)

        val literalNewLine = Evaluator("\"Hello\\nWorld\"").parse()
        assertEquals("Hello\nWorld", literalNewLine.value)

        val literalTab = Evaluator("\"Hello\\tWorld\"").parse()
        assertEquals("Hello\tWorld", literalTab.value)

        val literalSlash = Evaluator("\"Hello\\\\World").parse()
        assertEquals("Hello\\World", literalSlash.value)

        val literalSlashAtEnd = Evaluator("\"Hello\\\\").parse()
        assertEquals("Hello\\", literalSlashAtEnd.value)

        val plusEmpty = Evaluator("\"Hello\" + \"\"").parse()
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
    fun testConstantIdentifiers() {
        val pi = Evaluator("PI").parse()
        assertEquals(Math.PI, pi.value as Double, tiny)

        val e = Evaluator("E").parse()
        assertEquals(Math.E, e.value as Double, tiny)

        val tauMinus2Pi = Evaluator("TAU - 2 * PI").parse()
        assertEquals(0.0, tauMinus2Pi.value as Double, tiny)
    }

    @Test
    fun testFunctions() {
        val sqrt = Evaluator("sqrt(4)").parse()
        assertEquals(2.0, sqrt.value as Double, tiny)
    }

    @Test
    fun testCreateVector() {

        val a = Evaluator("Vector2(1,2)").parse() as Prop<Vector2>
        assertEquals(1.0, a.value.x, tiny)
        assertEquals(2.0, a.value.y, tiny)
    }

    @Test
    fun testAngles() {
        val a = Evaluator("Angle(0)").parse() as Prop<Angle>
        assertEquals(0.0, a.value.radians, tiny)
        assertEquals(0.0, a.value.degrees, tiny)

        val b = Evaluator("Angle(1)").parse() as Prop<Angle>
        assertEquals(1.0, b.value.radians, tiny)

        val c = Evaluator("Angle(1) + Angle(2)").parse() as Prop<Angle>
        assertEquals(3.0, c.value.radians, tiny)
    }

    @Test
    fun testVectorMaths() {
        //val plus = Evaluator()
    }
}
