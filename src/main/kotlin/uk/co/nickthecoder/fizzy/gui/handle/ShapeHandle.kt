package uk.co.nickthecoder.fizzy.gui.handle

import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape

// TODO Make this abstract
open class ShapeHandle(val shape: Shape, position: Dimension2)
    : Handle(position) {

    override fun isFor(shape: Shape) = shape === this.shape

    override fun dragTo(pagePosition: Dimension2, constrain: Boolean) {} // TODO Remove
}
