package uk.co.nickthecoder.fizzy.prop

interface PropListener<T> {
    fun changed(prop: Prop<T>)
}
