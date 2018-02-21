package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList

class Page : HasChangeListeners<Page> {

    override val listeners = ChangeListeners<Page>()

    var layers = MutableFList<Layer>()


    private var previousId = 0

    private val layersListener = ChangeAndCollectionListener(this, layers)


    fun generateId(): String {
        previousId++
        return "shape$previousId"
    }
}
