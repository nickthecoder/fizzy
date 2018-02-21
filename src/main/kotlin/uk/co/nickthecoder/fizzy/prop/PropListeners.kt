package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.util.Listeners

class PropListeners : Listeners<PropListener>() {

    fun fireDirty(prop: Prop<*>) {
        forEach { it.dirty(prop) }
    }

}
