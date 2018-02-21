package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener

class Geometry(val shape: Shape)

    : ChangeListener<GeometryPart>, CollectionListener<GeometryPart> {

    val listeners = ChangeListeners<Geometry>()

    var parts = MutableFList<GeometryPart>()

    val lineWidth = DimensionExpression("1mm")

    override fun changed(item: GeometryPart, changeType: ChangeType, obj: Any?) {
        listeners.fireChanged(this, ChangeType.CHANGE, item)
    }

    override fun added(collection: FCollection<GeometryPart>, item: GeometryPart) {
        item.listeners.add(this)
        listeners.fireChanged(this, ChangeType.CHANGE, collection)
    }

    override fun removed(collection: FCollection<GeometryPart>, item: GeometryPart) {
        item.listeners.remove(this)
        listeners.fireChanged(this, ChangeType.CHANGE, collection)
    }
}

abstract class GeometryPart(val geometry: Geometry)
    : PropListener {

    val listeners = ChangeListeners<GeometryPart>()

    override fun dirty(prop: Prop<*>) {
        listeners.fireChanged(this, ChangeType.CHANGE, prop)
    }
}

class MoveTo(geometry: Geometry)
    : GeometryPart(geometry) {

    val point = Dimension2Expression("Dimension2(0mm, 0mm)", geometry.shape.context)

    init {
        point.listeners.add(this)
    }
}

class LineTo(geometry: Geometry)
    : GeometryPart(geometry) {

    val point = Dimension2Expression("Dimension2(0mm, 0mm)")
}
