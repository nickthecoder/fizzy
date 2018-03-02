package uk.co.nickthecoder.fizzy.model.history

interface Change {

    fun undo()

    fun redo()

    fun mergeWith(other: Change): Boolean = false
}
