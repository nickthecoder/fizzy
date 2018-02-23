/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.fizzy.evaluator

import org.junit.Test
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestEvaluator : MyTestCase() {

    /**
     * I use this for single ad-hoc tests, and then when the test passes, I move it somewhere else, so
     * this test ends up being empty most of the time.
     */
    @Test
    fun testQuick() {
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

        val pow = Evaluator("3 ^ 2").parse() as Prop<Double>
        assertEquals(9.0, pow.value, tiny)
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

        val a = Evaluator("abs(0-2)").parse() as Prop<Double>
        assertEquals(2.0, a.value, tiny)

        val b = Evaluator("abs(-3)").parse() as Prop<Double>
        assertEquals(3.0, b.value, tiny)
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
    fun testConstructors() {
        // NOTE, at a later date, the evaluator may be improved such that (1+1) DOES return a PropConstant
        // and if so, the following tests would need to use *real* variables to test the !is cases PropConstant.

        //val a = Evaluator("Vector2(1,2)").parse()
        // FAILS : assert(a is PropConstant<*>) { "is ${a.javaClass}" }
        val a2 = Evaluator("Vector2(1 + 1,2)").parse()
        assert(a2 !is PropConstant<*>) { "is ${a2.javaClass}" }
        val a3 = Evaluator("Vector2(1,2+1)").parse()
        assert(a3 !is PropConstant<*>) { "is ${a3.javaClass}" }

        //val b = Evaluator("Dimension2(1m,2m)").parse()
        // FAILS : assert(b is PropConstant<*>) { "is ${a.javaClass}" }
        val b2 = Evaluator("Dimension2((1+1)m,2m)").parse()
        assert(b2 !is PropConstant<*>) { "is ${b2.javaClass}" }
        val b3 = Evaluator("Dimension2(1m,(2+1)m)").parse()
        assert(b3 !is PropConstant<*>) { "is ${b3.javaClass}" }

        val c = Evaluator("10 deg").parse()
        assert(c is PropConstant) { "is ${c.javaClass}" }
        val c2 = Evaluator("(10+1) deg").parse()
        assert(c2 !is PropConstant) { "is ${c2.javaClass}" }

        val d = Evaluator("10 rad").parse()
        assert(d is PropConstant) { "is ${d.javaClass}" }
        val d2 = Evaluator("(10+1) rad").parse()
        assert(d2 !is PropConstant) { "is ${d2.javaClass}" }

        val e = Evaluator("\"Hello\"").parse()
        assert(e is PropConstant) { "is ${e.javaClass}" }
        val e2 = Evaluator("\"Hello\" + \" World\"").parse()
        assert(e2 !is PropConstant) { "is ${e2.javaClass}" }

        val f = Evaluator("10m").parse()
        assert(f is PropConstant) { "is ${f.javaClass}" }
        val f2 = Evaluator("(10+1)m").parse()
        assert(f2 !is PropConstant) { "is ${f2.javaClass}" }
    }

    //variables.putProp("angle1", ExpressionProp("value1 degrees", Angle::class, context))

}
