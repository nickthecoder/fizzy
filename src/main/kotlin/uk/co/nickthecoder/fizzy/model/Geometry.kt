package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener

class Geometry(val shape: Shape)

    : HasChangeListeners<Geometry> {

    override val listeners = ChangeListeners<Geometry>()

    var parts = MutableFList<GeometryPart>()

    val lineWidth = DimensionExpression("1mm")

    private val geometryPartsListener = object : ChangeAndCollectionListener<Geometry, GeometryPart>(this, parts) {
        override fun added(collection: FCollection<GeometryPart>, item: GeometryPart) {
            super.added(collection, item)
            item.geometry = parent
        }

        override fun removed(collection: FCollection<GeometryPart>, item: GeometryPart) {
            super.removed(collection, item)
            item.geometry = null
        }
    }
}

abstract class GeometryPart()

    : HasChangeListeners<GeometryPart>, PropListener {

    internal var geometry: Geometry? = null
        set(v) {
            field = v
            if (v == null) {
                setContext(constantsContext)
            } else {
                setContext(v.shape.context)
            }
        }

    protected abstract fun setContext(context: Context)

    override val listeners = ChangeListeners<GeometryPart>()

    override fun dirty(prop: Prop<*>) {
        listeners.fireChanged(this, ChangeType.CHANGE, prop)
    }
}

class MoveTo()

    : GeometryPart() {

    val point = Dimension2Expression("Dimension2(0mm, 0mm)")

    init {
        point.listeners.add(this)
    }

    override fun setContext(context: Context) {
        point.context = context
    }
}

class LineTo()

    : GeometryPart() {

    val point = Dimension2Expression("Dimension2(0mm, 0mm)")

    init {
        point.listeners.add(this)
    }

    override fun setContext(context: Context) {
        point.context = context
    }
}
