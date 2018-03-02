package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.ShapeParent

class CreateShape(val copyFrom: Shape, val parent: ShapeParent, val position: Dimension2)
    : Change {

    lateinit var newShape: Shape

    override fun redo() {
        newShape = copyFrom.copyInto(parent)
        newShape.transform.pin.formula = position.toFormula()
    }

    override fun undo() {
        newShape.document().selection.remove(newShape)
        parent.children.remove(newShape)
    }
}