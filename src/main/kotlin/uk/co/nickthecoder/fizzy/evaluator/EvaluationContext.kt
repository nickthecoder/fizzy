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

import uk.co.nickthecoder.fizzy.model.Color
import uk.co.nickthecoder.fizzy.model.StrokeCap
import uk.co.nickthecoder.fizzy.model.StrokeJoin
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropType
import uk.co.nickthecoder.fizzy.prop.PropValue

interface EvaluationContext {

    fun findProp(name: String): Prop<*>?

    val thisProp: Prop<*>?

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

    override val thisProp: Prop<*>?
        get() {
            children.forEach { child ->
                if (child.thisProp != null) {
                    return child.thisProp
                }
            }
            return null
        }
}

class SimpleEvaluationContext(properties: Map<String, Prop<*>> = emptyMap())

    : EvaluationContext {

    val properties = mutableMapOf<String, Prop<*>>()

    override val thisProp: Prop<*>? = null

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
class ThisContext<T : Any>(val me: T, val type: PropType<T>)
    : EvaluationContext {

    override val thisProp = PropValue(me)

    override fun findProp(name: String): Prop<*>? {
        if (name == "this") return thisProp
        return type.findField(thisProp, name)
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
                "NaN" to PropConstant(Double.NaN),

                "STROKE_JOIN_BEVEL" to PropConstant(StrokeJoin.BEVEL),
                "STROKE_JOIN_MITER" to PropConstant(StrokeJoin.MITER),
                "STROKE_JOIN_ROUND" to PropConstant(StrokeJoin.ROUND),

                "STROKE_CAP_BUTT" to PropConstant(StrokeCap.BUTT),
                "STROKE_CAP_SQUARE" to PropConstant(StrokeCap.SQUARE),
                "STROKE_CAP_ROUND" to PropConstant(StrokeCap.ROUND),

                "BLACK" to PropConstant(Color.BLACK),
                "WHITE" to PropConstant(Color.WHITE),
                "TRANSPARENT" to PropConstant(Color.TRANSPARENT)
        )
)
