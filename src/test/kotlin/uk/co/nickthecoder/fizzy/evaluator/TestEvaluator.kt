package uk.co.nickthecoder.fizzy.evaluator

import junit.framework.TestCase
import org.junit.Test
import uk.co.nickthecoder.fizzy.prop.DoubleValue

class TestEvaluator : TestCase() {

    val tiny = 0.000001

    @Test
    fun testConstant() {
        val one = Evaluator("1").parse() as DoubleValue
        assertEquals(1.0, one.value, tiny)

        val two = Evaluator("  2  ").parse() as DoubleValue
        assertEquals(2.0, two.value, tiny)

        val oneAndAHalf = Evaluator("1.5").parse() as DoubleValue
        assertEquals(1.5, oneAndAHalf.value, tiny)
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
    fun testCreateVector() {
        //val con = Evaluator("Vector2").parse()
        //assertTrue(con is FConstructor)

        //val a = Evaluator("Vector2(1,2)").parse() as Prop<Vector2>
        //assertEquals(1.0, a.value.x, tiny)
        //assertEquals(2.0, a.value.y, tiny)
    }

    @Test
    fun testVectorMaths() {
        //val plus = Evaluator()
    }
}
