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

private fun <T : Any> evaluate(formula: String, klass: Class<T>, context: EvaluationContext): Prop<T> {

    val prop = Evaluator(formula, context).parse()
    val value = prop.value

    if (klass.isInstance(value) || klass.isPrimitive) {
        @Suppress("UNCHECKED_CAST")
        return prop as Prop<T>
    }
    /*
    // If we have a Dimension of power 0, then this can be coerced to a Double safely
    if (klass == Double::class.java && value is Dimension && value.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return if (prop.isConstant())
            PropConstant(value.inDefaultUnits) as Prop<T>
        else
            PropCalculation1(prop as Prop<Dimension>) { av -> av.inDefaultUnits } as Prop<T>

        // Likewise, if we have a Dimension2 of power 0, then this can be coerced to a Vector2
    } else if (klass == Vector2::class.java && value is Dimension2 && value.x.power == 0.0 && value.y.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return if (prop.isConstant())
            PropConstant(Vector2(value.x.inDefaultUnits, value.y.inDefaultUnits)) as Prop<T>
        else
            PropCalculation1(prop as Prop<Dimension2>) { av -> Vector2(av.x.inDefaultUnits, av.y.inDefaultUnits) } as Prop<T>
    }
    */

    throw EvaluationException("Expected type ${klass.simpleName}, but found ${prop.value.javaClass.simpleName}", 0)
}

abstract class PropExpression<T : Any>(
        formula: String,
        val klass: Class<T>,
        var context: EvaluationContext = constantsContext)

    : PropCalculation<T>() {

    constructor(other: PropExpression<T>, context: EvaluationContext = constantsContext)
            : this(other.formula, other.klass, context) {
        linkedTo = other
        other.propListeners.add(linkedListener)
    }

    private var linkedTo: PropExpression<T>? = null

    private var error: Boolean = false

    var formula: String = formula
        get() = linkedTo?.formula ?: field
        set(v) {
            field = v
            linkedTo?.propListeners?.remove(this)
            linkedTo = null
            forceRecalculation()
        }

    private val linkedListener = object : PropListener {
        override fun dirty(prop: Prop<*>) {
            forceRecalculation()
        }
    }

    abstract val defaultValue: T

    private var calculatedProperty: Prop<T>? = null

    var debug = false

    fun isLinked() = linkedTo != null

    fun isError() = error

    fun linkedTo(): PropExpression<T>? = linkedTo

    fun debug() {
        val cp = calculatedProperty
        println("\nPropExpression.debug")
        if (cp == null) {
            println("cp == null")
        } else {
            println("Formula '$formula'")
            println("cp # ${cp.hashCode()} type ${cp.javaClass.simpleName} = ${calculatedProperty}")
            println("Dirty? $dirty value = $value cp's value : ${cp.value}")
        }
        println()
    }


    override fun dirty(prop: Prop<*>) {
        if (debug) {
            println("PE # ${hashCode()} made dirty")
        }
        super.dirty(prop)
    }

    fun copyFrom(other: PropExpression<*>, link: Boolean) {
        if (link) {
            if (klass !== other.klass) throw IllegalStateException("PropExpressions are not of the same type : $this vs $other")
            @Suppress("UNCHECKED_CAST")
            linkedTo = other as PropExpression<T>

            other.propListeners.add(linkedListener)
            dirty = true
        } else {
            formula = other.formula
        }
    }

    override fun eval(): T {
        var cp = calculatedProperty

        if (cp == null) {
            try {
                cp = evaluate(formula, klass, context)
                calculatedProperty = cp
                listenTo(cp)
                error = false
                return cp.value
            } catch (e: Exception) {
                error = true
                expressionExceptionHandler(this, e)
                return defaultValue
            }

        } else {
            return cp.value
        }
    }

    fun forceRecalculation() {
        calculatedProperty?.propListeners?.remove(this)
        calculatedProperty = null
        dirty = true
    }

    abstract fun copy(link: Boolean): PropExpression<T>

    abstract fun valueString(): String

    override fun toString() = "='$formula'"
}

class PropExpressionException(propExpression: PropExpression<*>, e: Exception)
    : Exception("Error in expression ${propExpression.formula}", e)

var expressionExceptionHandler: (PropExpression<*>, Exception) -> Unit = { propExpression, exception ->
    throw PropExpressionException(propExpression, exception)
}
