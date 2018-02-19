package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.AbstractProp
import uk.co.nickthecoder.fizzy.prop.Prop

/**
 * Used to hold a list of arguments. The [ApplyOperator] "(", works on a SINGLE value, so if a function takes more than
 * one value, then the values are held in a single [ArgList].
 *
 * The "," operator converts a pair of non-ArgList value (such as Prop<Double> ), into an ArgList
 * The "," operator given an ArgList and an non-ArgList value adds the non-argList value to the list.
 *
 * In this way, when the ")" is found, there will be a single ArgList for the [ApplyOperator] to process.
 */
class ArgList : AbstractProp<MutableList<Prop<*>>>() {

    override val value = mutableListOf<Prop<*>>()

    override fun toString(): String {
        return "ArgList: $value"
    }

}
