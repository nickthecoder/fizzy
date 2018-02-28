package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.ConnectionPoint

class ConnectionPointPropType private constructor()
    : PropType<ConnectionPoint>(ConnectionPoint::class) {

    override fun findField(prop: Prop<ConnectionPoint>, name: String): Prop<*>? {
        return when (name) {
            "Point" -> prop.value.point
            else -> super.findField(prop, name)
        }
    }

    companion object {
        val instance = ConnectionPointPropType()
    }
}
