package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.list.MutableFList

open class Group

    : Shape(), Parent {

    override var children = MutableFList<Shape>()

}
