package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.*

/**
 * Identifiers that are placed AFTER the argument being manipulated, used to convert the value into one with units.
 * e.g. "5 cm" converts the Double 5 into a Dimension with units of centimeters.
 */
object Conversions {

    val conversions = mutableMapOf<String, (Prop<*>) -> Prop<*>>(
            "deg" to ::degConversion, // Double -> Angle (degrees)
            "rad" to ::radConversion, // Double -> Angle (radians)
            "mm" to ::mmConversion, // Double -> Dimension (millimeters)
            "cm" to ::cmConversion, // Double -> Dimension (centimeters)
            "m" to ::mConversion, // Double -> Dimension (meters)
            "km" to ::kmConversion // Double -> Dimension (kilometers)
    )

    fun find(str: String): ((Prop<*>) -> Prop<*>)? = conversions[str]
}
