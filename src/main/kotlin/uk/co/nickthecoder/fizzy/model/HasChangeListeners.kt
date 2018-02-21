package uk.co.nickthecoder.fizzy.model

/**
 * Used by [ChangeAndCollectionListener].
 */
interface HasChangeListeners<T> {
    val listeners: ChangeListeners<T>
}
