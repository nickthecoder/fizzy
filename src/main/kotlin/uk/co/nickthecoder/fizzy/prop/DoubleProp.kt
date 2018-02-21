package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext

class DoublePropType private constructor()
    : PropType<Double>(Double::class) {

    override fun findField(prop: Prop<Double>, name: String): PropField<Double, *>? {
        return null
    }

    override fun findMethod(prop: Prop<Double>, name: String): PropMethod<Double, *>? {
        return null
    }

    companion object {
        val instance = DoublePropType()
    }
}

class DoubleExpression(expression: String, context: Context = constantsContext)
    : PropExpression<Double>(expression, Double::class, context)
