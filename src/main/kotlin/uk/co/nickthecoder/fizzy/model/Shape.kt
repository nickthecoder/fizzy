package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.util.runLater

abstract class Shape(var parent: Parent)
    : PropListener, HasChangeListeners<Shape> {

    var id = PropConstant(parent.page().generateId())

    abstract val context: Context

    override var listeners = ChangeListeners<Shape>()

    val geometry = Geometry(this)

    val geometryListener = object : ChangeListener<Geometry> {
        override fun changed(item: Geometry, changeType: ChangeType, obj: Any?) {
            dirty = true
        }
    }

    init {
        id.listeners.add(this)
        parent.children.add(this)
        geometry.listeners.add(geometryListener)
    }

    private var dirty = false
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    runLater {
                        dirty = false
                        listeners.fireChanged(this)
                    }
                }
            }
        }

    override fun dirty(prop: Prop<*>) {
        dirty = true
    }

    fun page(): Page = parent.page()

    fun layer(): Layer = parent.layer()

    override fun toString(): String = "Shape ${id.value}"

}
