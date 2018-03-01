package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class Scratch(name: String, expression: String) {

    val name = PropVariable<String>(name)

    val expression = PropExpression<Any>(expression, Any::class)

    /**
     * Used only as documentation of the Master Shape, and is NOT available for use in expressions.
     * Therefore it is not a [Prop].
     */
    var comment = ""

    fun setContext(context: EvaluationContext) {
        expression.context = context
    }
}

class ScratchProp(scratch: Scratch)
    : PropValue<Scratch>(scratch),
        PropListener,
        HasChangeListeners<ScratchProp> {

    override val changeListeners = ChangeListeners<ScratchProp>()

    init {
        scratch.name.propListeners.add(this)
        scratch.expression.propListeners.add(this)
    }

    /**
     * Any changes to Scratch's data causes this [Prop]'s propListeners to be notified.
     * The [ScratchProp]'s constructor adds itself to the listeners of each of [Scratch]'s [Prop]s.
     */
    override fun dirty(prop: Prop<*>) {
        propListeners.fireDirty(this)
    }
}
