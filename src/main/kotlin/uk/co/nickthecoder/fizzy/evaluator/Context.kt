package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.*

interface Context {

    fun findFunction(name: String): Function?

    fun findProp(name: String): Prop<*>?

}

class CompoundContext(val children: List<Context>) : Context {

    override fun findFunction(name: String): Function? {
        children.forEach {
            val result = it.findFunction(name)
            if (result != null) {
                return result
            }
        }
        return null
    }

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

class SimpleContext(
        properties: Map<String, Prop<*>> = emptyMap(),
        functions: Map<String, Function> = emptyMap())

    : Context {

    val properties = mutableMapOf<String, Prop<*>>()

    val functions = mutableMapOf<String, Function>()

    init {
        this.properties.putAll(properties)
        this.functions.putAll(functions)
    }

    override fun findFunction(name: String) = functions[name]

    override fun findProp(name: String) = properties[name]

    fun putFunction(name: String, function: Function) {
        functions.put(name, function)
    }

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
        ),
        mapOf(
                "Vector2" to NewVector2(),
                "Dimension2" to NewDimension2(),
                "sqrt" to Sqrt(),
                "ratio" to Ratio()
        )
)
