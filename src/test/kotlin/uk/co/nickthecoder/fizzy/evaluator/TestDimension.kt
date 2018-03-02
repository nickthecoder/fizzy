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
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestDimension : MyTestCase() {

    @Test
    fun testGeneral() {
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

        val g = Evaluator("10mm % 2mm").parse() as Prop<Double>
        assertEquals(5.0, g.value, tiny)

        val i = Evaluator("(20cm * 20cm) % (200cm * 4cm)").parse() as Prop<Double>
        assertEquals(0.5, i.value, tiny)

        val j = Evaluator("(0.2m * 0.2m) % (200cm * 4cm)").parse() as Prop<Double>
        assertEquals(0.5, j.value, tiny)

    }

    @Test
    fun testIsConstant() {

        val f = Evaluator("10m").parse()
        assert(f is PropConstant) { "is ${f.javaClass}" }
        assertEquals(true, f.isConstant())
        val f2 = Evaluator("(10+1)m").parse()
        assert(f2 !is PropConstant) { "is ${f2.javaClass}" }
        assertEquals(false, f2.isConstant())
    }

    @Test
    fun testDynamic() {

        val variables = SimpleEvaluationContext()
        val context = CompoundEvaluationContext(listOf(constantsContext, variables))

        variables.putProp("x", DoubleExpression("3", context))
        variables.putProp("y", DoubleExpression("4", context))

        variables.putProp("width", DimensionExpression("x mm", context))
        variables.putProp("height", DimensionExpression("y cm", context))

        val a = Evaluator("width + height", context).parse() as Prop<Dimension>
        val x = variables.findProp("x") as PropExpression<Double>

        assertEquals(43.0, a.value.mm, tiny)
        x.formula = "5"
        assertEquals(45.0, a.value.mm, tiny)
    }

    @Test
    fun testFields() {
        val a = Evaluator("15mm.mm").parse() as Prop<Double>
        assertEquals(15.0, a.value, tiny)

        val b = Evaluator("15mm.cm").parse() as Prop<Double>
        assertEquals(1.5, b.value, tiny)

        val c = Evaluator("15mm.m").parse() as Prop<Double>
        assertEquals(0.015, c.value, tiny)

        val d = Evaluator("15mm.km").parse() as Prop<Double>
        assertEquals(0.000015, d.value, tiny)
    }

}