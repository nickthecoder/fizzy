package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropType

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

/**
 * All fields from a given property [me] are exposed as top-level properties.
 *
 * This is used by Shape, so that its expressions can reference part of the Shape
 * without the need for "this.".
 */
class ThisContext<T : Any>(val me: Prop<T>, val type: PropType<T>)
    : Context {

    override fun findProp(name: String): Prop<*>? {
        return type.findField(me, name)
    }
}

val constantsContext = SimpleContext(
        mapOf(
                "PI" to PropConstant(Angle.PI),
                "TAU" to PropConstant(Angle.TAU),
                "E" to PropConstant(Math.E),
                "MAX_DOUBLE" to PropConstant(Double.MAX_VALUE),
                "MIN_DOUBLE" to PropConstant(-Double.MAX_VALUE), // Note, this is NOT the same as the badly named Java Double.MIN_VALUE
                "SMALLEST_DOUBLE" to PropConstant(Double.MIN_VALUE),
                "NaN" to PropConstant(Double.NaN)
        )
)
