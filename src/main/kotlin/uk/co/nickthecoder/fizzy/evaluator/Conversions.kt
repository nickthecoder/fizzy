package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.*

object Conversions {

    val conversions = mutableMapOf<String, (Prop<*>) -> Prop<*>>(
            "deg" to ::degConversion,
            "rad" to ::radConversion,
            "mm" to ::mmConversion,
            "cm" to ::cmConversion,
            "m" to ::mConversion,
            "km" to ::kmConversion
    )

    fun find(str: String): ((Prop<*>) -> Prop<*>)? = conversions[str]
}
