package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.CompoundContext
import uk.co.nickthecoder.fizzy.evaluator.SimpleContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.prop.StringConstant
import uk.co.nickthecoder.fizzy.util.runLater

open class Shape(var parent: Parent)
    : PropListener, HasChangeListeners<Shape> {

    var id = StringConstant(parent.page().generateId())

    val context = CompoundContext(listOf(constantsContext, SimpleContext(mapOf(
            "this" to PropConstant<Shape>(this),
            "layer" to PropConstant<Layer>(parent.layer()),
            "page" to PropConstant<Page>(parent.page())
    ))))

    override var listeners = ChangeListeners<Shape>()

    val geometry = Geometry(this)

    val geometryListener = object : ChangeListener<Geometry> {
        override fun changed(item: Geometry, changeType: ChangeType, obj: Any?) {
            dirty = true
        }
    }

    init {
        parent.children.add(this)
        geometry.listeners.add(geometryListener)
    }

    private var dirty = false
        set(v) {
            if (field != v) {
                field = v
                if (v) {
                    runLater {
                        field = false
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
