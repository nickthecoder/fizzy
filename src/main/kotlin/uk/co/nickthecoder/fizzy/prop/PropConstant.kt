package uk.co.nickthecoder.fizzy.prop

/**
 * A property whose value can be changed (i.e. it is a var).
 */
abstract class PropConstant<T>(value: T) : AbstractProp<T>() {

    override var value: T = value
        set(v) {
            if (v != field) {
                field = v
                listeners.forEach { it.dirty(this) }
            }
        }

    override fun isConstant() = true

}
