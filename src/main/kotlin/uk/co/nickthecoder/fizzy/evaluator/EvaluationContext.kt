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

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropType

interface EvaluationContext {

    fun findProp(name: String): Prop<*>?

}

class CompoundEvaluationContext(val children: List<EvaluationContext>) : EvaluationContext {

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

class SimpleEvaluationContext(properties: Map<String, Prop<*>> = emptyMap())

    : EvaluationContext {

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
    : EvaluationContext {

    override fun findProp(name: String): Prop<*>? {
        return type.findField(me, name)
    }
}

val constantsContext = SimpleEvaluationContext(
        mapOf(
                "true" to PropConstant(true),
                "false" to PropConstant(false),
                "PI" to PropConstant(Math.PI),
                "TAU" to PropConstant(Math.PI * 2),
                "E" to PropConstant(Math.E),
                "MAX_DOUBLE" to PropConstant(Double.MAX_VALUE),
                "MIN_DOUBLE" to PropConstant(-Double.MAX_VALUE), // Note, this is NOT the same as the badly named Java Double.MIN_VALUE
                "SMALLEST_DOUBLE" to PropConstant(Double.MIN_VALUE),
                "NaN" to PropConstant(Double.NaN)
        )
)
