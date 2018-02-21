package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.util.Listeners

class ChangeListeners<T> : Listeners<ChangeListener<T>>() {

    fun fireChanged(item: T, changeType: ChangeType = ChangeType.CHANGE, obj: Any? = null) {
        forEach { it.changed(item, changeType, obj) }
    }

    companion object {
        private val forever = mutableListOf <Any>()
    }
}
