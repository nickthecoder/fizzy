package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.AngleConstant
import uk.co.nickthecoder.fizzy.prop.DoubleConstant
import uk.co.nickthecoder.fizzy.prop.Prop

interface Context {

    fun findProp(name: String): Prop<*>?

}

class CompoundContext(val children: List<Context>) : Context {

    override fun findProp(name: String): Prop<*>? {
        children.forEach {
            val result = it.findProp(name)
            if (result != null) {
                return result
            }
        }
        return null
    }
}

class SimpleContext(properties: Map<String, Prop<*>> = emptyMap())

    : Context {

    val properties = mutableMapOf<String, Prop<*>>()

    init {
        this.properties.putAll(properties)
    }

    override fun findProp(name: String) = properties[name]

    fun putProp(name: String, prop: Prop<*>) {
        properties.put(name, prop)
    }
}

val constantsContext = SimpleContext(
        mapOf(
                "PI" to AngleConstant(Angle.PI),
                "TAU" to AngleConstant(Angle.TAU),
                "E" to DoubleConstant(Math.E),
                "MAX_DOUBLE" to DoubleConstant(Double.MAX_VALUE),
                "MIN_DOUBLE" to DoubleConstant(-Double.MAX_VALUE), // Note, this is NOT the same as the badly named Java Double.MIN_VALUE
                "SMALLEST_DOUBLE" to DoubleConstant(Double.MIN_VALUE),
                "NaN" to DoubleConstant(Double.NaN)
        )
)
