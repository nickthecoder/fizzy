package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.Dimension2Expression

class Shape1d(parent: Parent)
    : Shape(parent) {

    val start = Dimension2Expression("Dimension2(0mm,0mm)", context)

    val end = Dimension2Expression("Dimension2(1mm,1mm)", context)

    init {
        start.listeners.add(this)
        end.listeners.add(this)
    }
}
