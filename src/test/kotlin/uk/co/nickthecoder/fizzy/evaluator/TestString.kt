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
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.util.MyTestCase

@Suppress("UNCHECKED_CAST")
class TestString : MyTestCase() {

    @Test
    fun testGeneral() {

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
    fun testIsConstant() {

        val e = Evaluator("\"Hello\"").parse()
        assert(e is PropConstant) { "is ${e.javaClass}" }
        assertEquals(true, e.isConstant())
        val e2 = Evaluator("\"Hello\" + \" World\"").parse()
        assert(e2 !is PropConstant) { "is ${e2.javaClass}" }
        assertEquals(false, e2.isConstant())

    }

    @Test
    fun testFields() {
        val a = Evaluator("\"Hello\".Length").parse() as Prop<Double>
        assertEquals(5.0, a.value, tiny)
    }

    @Test
    fun testMethods() {
        val a = Evaluator("\"Hello\".substring(2,5)").parse() as Prop<String>
        assertEquals("llo", a.value)

        val b = Evaluator("\"Hello\".head(2)").parse() as Prop<String>
        assertEquals("He", b.value)

        val c = Evaluator("\"Hello\".tail(2)").parse() as Prop<String>
        assertEquals("lo", c.value)

        val d = Evaluator("\"Hello\".head(20)").parse() as Prop<String>
        assertEquals("Hello", d.value)

        val e = Evaluator("\"Hello\".tail(20)").parse() as Prop<String>
        assertEquals("Hello", e.value)

        val f1 = Evaluator("\"Hello\".substring(3,5)").parse() as Prop<String>
        assertEquals("lo", f1.value)
        val f2 = Evaluator("\"Hello\".substring(4,6)").parse() as Prop<String>
        assertEquals("o", f2.value)
        val f3 = Evaluator("\"Hello\".substring(5,7)").parse() as Prop<String>
        assertEquals("", f3.value)
        val f4 = Evaluator("\"Hello\".substring(6,8)").parse() as Prop<String>
        assertEquals("", f4.value)
        val f5 = Evaluator("\"Hello\".substring(20,22)").parse() as Prop<String>
        assertEquals("", f5.value)

        val g1 = Evaluator("\"Hello\".substring(0,2)").parse() as Prop<String>
        assertEquals("He", g1.value)
        val g2 = Evaluator("\"Hello\".substring(-1,1)").parse() as Prop<String>
        assertEquals("H", g2.value)
        val g3 = Evaluator("\"Hello\".substring(-2,0)").parse() as Prop<String>
        assertEquals("", g3.value)
        val g4 = Evaluator("\"Hello\".substring(-3,-1)").parse() as Prop<String>
        assertEquals("", g4.value)

        val h4 = Evaluator("\"Hello\".substring(2,1)").parse() as Prop<String>
        assertEquals("", g4.value)
    }


}
