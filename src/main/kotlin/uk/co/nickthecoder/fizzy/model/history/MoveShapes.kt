package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape

class MoveShapes(shapes: List<Shape>, val delta: Dimension2) : Change {

    class OldAndNew(val shape: Shape, val oldExpression: String, var newValue: Dimension2)

    val changes = mutableMapOf<Int, OldAndNew>()

    init {
        shapes.forEach { shape ->
            addShape(shape, delta)
        }
    }

    fun addShape(shape: Shape, delta: Dimension2) {
        changes[shape.id.value] = OldAndNew(
                shape,
                shape.transform.pin.expression,
                shape.transform.pin.value + delta)
    }

    override fun redo() {
        changes.values.forEach {
            it.shape.transform.pin.expression = it.newValue.toString()
        }
    }

    override fun undo() {
        changes.values.forEach {
            it.shape.transform.pin.expression = it.oldExpression
        }
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is MoveShapes) {
            changes.values.forEach { myOldAndNew ->
                val otherOldAndNew = other.changes[myOldAndNew.shape.id.value]
                if (otherOldAndNew == null) {
                    other.addShape(myOldAndNew.shape, delta)
                } else {
                    otherOldAndNew.newValue += delta
                }
            }
            return true
        }
        return false
    }
}
