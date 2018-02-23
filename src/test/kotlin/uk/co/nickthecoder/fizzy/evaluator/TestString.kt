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
        val a = Evaluator("\"Hello\".length").parse() as Prop<Double>
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
    }

}
