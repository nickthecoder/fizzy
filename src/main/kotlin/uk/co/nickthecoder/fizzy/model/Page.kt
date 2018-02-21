package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList

class Page {

    val listeners = ChangeListeners<Page>()

    var layers = MutableFList<Layer>()


    private var previousId = 0

    private val layerListener = object : ChangeListener<Layer>, CollectionListener<Layer> {

        override fun changed(item: Layer, changeType: ChangeType, obj: Any?) {
            listeners.fireChanged(this@Page, changeType, obj)
        }

        override fun added(collection: FCollection<Layer>, item: Layer) {
            listeners.fireChanged(this@Page, ChangeType.ADD, item)
            item.listeners.add(this)
        }

        override fun removed(collection: FCollection<Layer>, item: Layer) {
            listeners.fireChanged(this@Page, ChangeType.REMOVE, item)
            item.listeners.remove(this)
        }
    }

    init {
        layers.listeners.add(layerListener)
    }


    fun generateId(): String {
        previousId++
        return "shape$previousId"
    }
}
