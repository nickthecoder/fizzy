package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.AngleExpression
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class ConnectionPoint(point: String, angle: String) :
        HasChangeListeners<ConnectionPoint> {

    override val listeners = ChangeListeners<ConnectionPoint>()

    val point = Dimension2Expression(point)

    /**
     * The preferred angle of lines coming out of this ConnectionPoint.
     */
    val direction = AngleExpression(angle)

    var shape: Shape? = null
        set(v) {
            if (field != v) {
                field?.let {
                    point.listeners.remove(it)
                    direction.listeners.remove(it)
                }
                field = v

                val context = v?.context ?: constantsContext
                point.context = context
                direction.context = context

                if (v != null) {
                    point.listeners.add(v)
                    direction.listeners.add(v)
                }
            }
        }
}
