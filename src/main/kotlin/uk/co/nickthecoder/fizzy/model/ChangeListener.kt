package uk.co.nickthecoder.fizzy.model

enum class ChangeType { CHANGE, ADD, REMOVE }

interface ChangeListener<in T> {

    fun changed(item: T, changeType: ChangeType = ChangeType.CHANGE, obj: Any? = null)

}
