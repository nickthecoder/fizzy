package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class ConnectionPoint(point: String, angle: String) {

    val point = Dimension2Expression(point)

    /**
     * The preferred angle of lines coming out of this ConnectionPoint.
     */
    val direction = AngleExpression(angle)

    var shape: Shape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    point.propListeners.remove(it)
                    direction.propListeners.remove(it)
                }
                field = v

                val context = v?.context ?: constantsContext
                point.context = context
                direction.context = context

                if (v != null) {
                    point.propListeners.add(v)
                    direction.propListeners.add(v)
                }
            }
        }

}

class ConnectionPointProp(connectionPoint: ConnectionPoint)
    : PropValue<ConnectionPoint>(connectionPoint), PropListener,
        HasChangeListeners<ConnectionPointProp> {

    override val changeListeners = ChangeListeners<ConnectionPointProp>()

    init {
        connectionPoint.point.propListeners.add(this)
        connectionPoint.direction.propListeners.add(this)
    }

    override fun dirty(prop: Prop<*>) {
        propListeners.fireDirty(this)
    }
}
