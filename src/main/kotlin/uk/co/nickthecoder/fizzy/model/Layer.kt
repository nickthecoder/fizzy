package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList

class Layer(val page: Page)
    : Parent, HasChangeListeners<Layer> {

    override val listeners = ChangeListeners<Layer>()

    override var children = MutableFList<Shape>()

    private val shapesListener = ChangeAndCollectionListener(this, children)

    init {
        page.layers.add(this)
    }

    override fun page() = page

    override fun layer() = this

}
