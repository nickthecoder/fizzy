package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.model.Shape

class DeleteShape(val shape: Shape) : Change {

    override fun redo() {
        shape.document().selection.remove(shape)
        shape.parent.children.remove(shape)
    }

    override fun undo() {
        shape.parent.children.add(shape)
    }
}
