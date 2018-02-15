package uk.co.nickthecoder.fizzy.prop

class LinkedProp<T>(linkTo: Prop<T>)

    : Prop<T>(linkTo.value), PropListener<T> {

    var linkTo: Prop<T> = linkTo
        set(v) {
            field = v
            value = v.value
        }

    init {
        linkTo.listeners.add(this)
    }

    override fun changed(prop: Prop<T>) {
        value = prop.value
    }
}
