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
package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import uk.co.nickthecoder.fizzy.evaluator.Evaluator
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import kotlin.reflect.KClass

private fun <T : Any> evaluate(expression: String, klass: KClass<T>, context: EvaluationContext): Prop<T> {

    val prop = Evaluator(expression, context).parse()
    val value = prop.value

    if (klass.isInstance(value)) {
        @Suppress("UNCHECKED_CAST")
        return prop as Prop<T>
    }
    // If we have a Dimension of power 0, then this can be coerced to a Double safely
    if (klass == Double::class && value is Dimension && value.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return PropConstant(value.inDefaultUnits) as Prop<T>

        // Likewise, if we have a Dimension2 of power 0, then this can be coerced to a Vector2
    } else if (klass == Vector2::class && value is Dimension2 && value.x.power == 0.0 && value.y.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return PropConstant(Vector2(value.x.inDefaultUnits, value.y.inDefaultUnits)) as Prop<T>
    }

    throw EvaluationException("Expected type ${klass.simpleName}, but found ${prop.value?.javaClass?.kotlin?.simpleName}", 0)
}

abstract class PropExpression<T : Any>(expression: String, val klass: KClass<T>, var context: EvaluationContext = constantsContext)

    : PropCalculation<T>() {

    var expression: String = expression
        set(v) {
            field = v
            dirty = true
        }

    var calculatedProperty: Prop<T>? = null

    override fun eval(): T {
        // TODO calculatedProperty?.listeners?.remove(this)
        val cp = evaluate(expression, klass, context)
        calculatedProperty = cp
        cp.listeners.add(this)
        return cp.value
    }

    override fun dump(): String {
        return "Expression : '$expression'"
    }
}
