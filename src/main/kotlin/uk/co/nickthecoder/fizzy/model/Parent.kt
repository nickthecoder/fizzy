package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList

interface Parent {

    val children: MutableFList<Shape>

    fun layer(): Layer

    fun page(): Page

}
