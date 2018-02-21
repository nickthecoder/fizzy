package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener

class Geometry(val shape: Shape)

    : HasChangeListeners<Geometry> {

    override val listeners = ChangeListeners<Geometry>()

    var parts = MutableFList<GeometryPart>()

    val lineWidth = DimensionExpression("1mm")

    private val geometryPartsListener = ChangeAndCollectionListener(this, parts)
}

abstract class GeometryPart(val geometry: Geometry)

    : HasChangeListeners<GeometryPart>, PropListener {

    override val listeners = ChangeListeners<GeometryPart>()

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
