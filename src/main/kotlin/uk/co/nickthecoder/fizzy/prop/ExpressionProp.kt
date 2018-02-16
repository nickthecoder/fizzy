package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import uk.co.nickthecoder.fizzy.evaluator.Evaluator
import kotlin.reflect.KClass

private fun <T : Any> evaluate(expression: String, klass: KClass<T>): Prop<T> {
    val prop = Evaluator(expression).parse()
    if (klass.isInstance(prop.value)) {
        throw EvaluationException("Expected type ${klass.simpleName}, but found ${prop.value?.javaClass?.kotlin?.simpleName}", 0)
    }
    @Suppress("UNCHECKED_CAST")
    return prop as Prop<T>
}

class ExpressionProp<T : Any>(expression: String, val klass: KClass<T>, initialValue: T)

    : PropCalculation<T>(initialValue) {

    var expression: String = expression
        set(v) {
            field = v
            dirty = true
        }

    override fun eval() {
        calculatedValue = evaluate(expression, klass).value
    }
}
