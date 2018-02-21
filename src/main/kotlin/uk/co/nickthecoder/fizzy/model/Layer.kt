package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList

class Layer(val page: Page)
    : Parent {

    val listeners = ChangeListeners<Layer>()

    override var children = MutableFList<Shape>()

    private val shapeListener = object : CollectionListener<Shape>, ChangeListener<Shape> {

        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            listeners.fireChanged(this@Layer, changeType, item)
        }

        override fun added(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@Layer, ChangeType.ADD, item)
            item.listeners.add(this)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@Layer, ChangeType.REMOVE, item)
            item.listeners.remove(this)
        }
    }

    init {
        page.layers.add(this)
        children.listeners.add(shapeListener)
    }

    override fun page() = page

    override fun layer() = this

}
