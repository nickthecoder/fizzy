package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.AbstractProp
import uk.co.nickthecoder.fizzy.prop.PropField
import uk.co.nickthecoder.fizzy.prop.PropType

/**
 * When an identifier token (such as "foo") is found, if the topmost operator is a [DotOperator], then
 * the identifier token is converted to a [FieldOrMethodName] and pushed onto the [Evaluator.values] stack.
 *
 * If there is no "(", then applying the [DotOperator] creates a [PropField].
 * The correct [PropField] is found by finding the KClass of the value to the left of the ".". The KClass, and the
 * [FieldOrMethodName.value] is passed to [PropType.field].
 *
 * At the time of writing this methods haven't been implemented yet, so that process is not clear!
 *
 * [FieldOrMethodName]s are temporary values placed on the values stack, but do not form part of the final
 * structure from [Evaluator.parse].
 */
class FieldOrMethodName(name: String) : AbstractProp<String>() {

    override val value = name

    override fun toString(): String {
        return "FieldOrMethodName : $value"
    }
}
