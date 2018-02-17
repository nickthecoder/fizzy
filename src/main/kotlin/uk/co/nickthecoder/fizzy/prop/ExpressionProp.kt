package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import uk.co.nickthecoder.fizzy.evaluator.Evaluator
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import kotlin.reflect.KClass

private fun <T : Any> evaluate(expression: String, klass: KClass<T>): Prop<T> {
    val prop = Evaluator(expression).parse()
    val value = prop.value
    if (klass.isInstance(value)) {
        @Suppress("UNCHECKED_CAST")
        return prop as Prop<T>
    }
    // If we have a Dimension of power 0, then this can be coerced to a Double safely
    if (klass == Double::class && value is Dimension && value.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return DoubleProp(value.inDefaultUnits) as Prop<T>

        // Likewise, if we have a Dimension2 of power 0, then this can be coerced to a Vector2
    } else if (klass == Vector2::class && value is Dimension2 && value.x.power == 0.0 && value.y.power == 0.0) {
        @Suppress("UNCHECKED_CAST")
        return Vector2Prop(Vector2(value.x.inDefaultUnits, value.y.inDefaultUnits)) as Prop<T>
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
