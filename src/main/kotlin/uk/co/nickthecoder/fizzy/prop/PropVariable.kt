package uk.co.nickthecoder.fizzy.prop

class PropVariable<T : Any>(initialValue: T)
    : AbstractProp<T>() {

    override var value: T = initialValue
        set(v) {
            if (field !== v) {
                field = v
                propListeners.fireDirty(this)
            }
        }
}
