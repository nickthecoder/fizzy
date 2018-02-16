package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import uk.co.nickthecoder.fizzy.evaluator.Evaluator
import uk.co.nickthecoder.fizzy.model.Dimension
import kotlin.reflect.KClass

private fun <T : Any> evaluate(expression: String, klass: KClass<T>): Prop<T> {
    val prop = Evaluator(expression).parse()
    val value = prop.value
    if (klass.isInstance(value)) {
        @Suppress("UNCHECKED_CAST")
        return prop as Prop<T>
    }
    if (klass == Double::class && value is Dimension && value.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return DoubleProp(value.number) as Prop<T>
    }
    throw EvaluationException("Expected type ${klass.simpleName}, but found ${prop.value?.javaClass?.kotlin?.simpleName}", 0)
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
