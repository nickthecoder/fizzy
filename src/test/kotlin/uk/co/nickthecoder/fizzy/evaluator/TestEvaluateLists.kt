package uk.co.nickthecoder.fizzy.evaluator

import junit.framework.TestCase
import org.junit.Test
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropMethod
import uk.co.nickthecoder.fizzy.prop.PropType
import java.lang.RuntimeException

@Suppress("UNCHECKED_CAST")
class TestEvaluateLists : TestCase() {

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

    private class Foo(val angles: FList<Angle>)

    private class FooPropType : PropType<Foo>(Foo::class) {

        override fun findField(prop: Prop<Foo>, name: String): Prop<*>? {
            if (name == "angles") {
                return PropConstant(prop.value.angles)
            }
            return super.findField(prop, name)
        }

        override fun findMethod(prop: Prop<Foo>, name: String): PropMethod<Foo, *>? = null
    }

    @Test
    fun testLists() {
        PropType.put(FooPropType())
        val foo = Foo(FList(listOf(Angle.degrees(45.0), Angle.degrees(90.0))))
        val context = SimpleContext(mapOf("foo" to PropConstant(foo)))

        val a = Evaluator("foo.angles1.degrees", context).parse() as Prop<Double>
        assertEquals(45.0, a.value, tiny)

        val b = Evaluator("foo.angles2.degrees", context).parse() as Prop<Double>
        assertEquals(90.0, b.value, tiny)

        assertFailsAt(3) {
            Evaluator("foo.angles0.degrees", context).parse()
        }
        assertFailsAt(3) {
            Evaluator("foo.angles3.degrees", context).parse()
        }

    }

}