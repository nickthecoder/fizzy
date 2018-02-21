package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.CompoundContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.ShapeGroupPropType

class ShapeGroup(parent: Parent)

    : Shape(parent), Parent {

    override val context = CompoundContext(listOf(
            constantsContext, ThisContext(PropConstant(this), ShapeGroupPropType.instance)))

    override var children = MutableFList<Shape>()


    private val shapeListener = object : ChangeListener<Shape>, CollectionListener<Shape> {

        override fun changed(item: Shape, changeType: ChangeType, obj: Any?) {
            listeners.fireChanged(this@ShapeGroup)
        }

        override fun added(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@ShapeGroup)
            item.listeners.add(this)
        }

        override fun removed(collection: FCollection<Shape>, item: Shape) {
            listeners.fireChanged(this@ShapeGroup)
            item.listeners.add(this)
        }
    }

    init {
        children.listeners.add(shapeListener)
    }

}
