/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Shape

class MoveShapes(shapes: List<Shape>, val delta: Dimension2)
    : Change {

    class OldAndNew(val shape: Shape, val oldFormula: String, var newValue: Dimension2)

    val changes = mutableMapOf<Int, OldAndNew>()

    init {
        shapes.forEach { shape ->
            addShape(shape, delta)
        }
    }

    fun addShape(shape: Shape, delta: Dimension2) {
        changes[shape.id.value] = OldAndNew(
                shape,
                shape.transform.pin.formula,
                shape.transform.pin.value + delta)
    }

    override fun redo() {
        changes.values.forEach {
            it.shape.transform.pin.formula = it.newValue.toFormula()
        }
    }

    override fun undo() {
        changes.values.forEach {
            it.shape.transform.pin.formula = it.oldFormula
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
