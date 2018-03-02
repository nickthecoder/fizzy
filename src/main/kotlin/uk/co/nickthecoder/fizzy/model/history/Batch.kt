package uk.co.nickthecoder.fizzy.model.history

class Batch {

    val changes = mutableListOf<Change>()

    fun undo() {
        changes.reversed().forEach { it.undo() }
    }

    fun redo() {
        changes.forEach { it.redo() }
    }

    fun makeChange(change: Change) {
        change.redo()
        val last = changes.lastOrNull()
        if (last != null && change.mergeWith(last)) {
            // Do nothing
        } else {
            changes.add(change)
        }
    }
}


