package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.Prop

object Conversions {

    val conversions = mutableMapOf<String, (Prop<*>) -> Prop<*>>(
            "deg" to ::DEG,
            "rad" to ::RAD
    )

    fun find(str: String): ((Prop<*>) -> Prop<*>)? = conversions[str]
}

private fun expected(type: String, found: Prop<*>): Prop<*> {
    throw RuntimeException("Expected a $type, but found ${found.value?.javaClass?.simpleName}")
}

private fun DEG(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        return Prop<Angle>(Angle.degrees(a.value as Double))
    }
    return expected("Double", a)
}

private fun RAD(a: Prop<*>): Prop<*> {
    if (a.value is Double) {
        return Prop<Angle>(Angle.radians(a.value as Double))
    }
    return expected("Double", a)
}
