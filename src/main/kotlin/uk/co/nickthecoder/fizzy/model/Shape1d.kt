package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.CompoundContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.Shape1dPropType

class Shape1d(parent: Parent)
    : Shape(parent) {

    override val context = CompoundContext(listOf(
            constantsContext, ThisContext(PropConstant(this), Shape1dPropType.instance)))

    val start = Dimension2Expression("Dimension2(0mm,0mm)", context)

    val end = Dimension2Expression("Dimension2(1mm,1mm)", context)

    init {
        start.listeners.add(this)
        end.listeners.add(this)
    }
}
