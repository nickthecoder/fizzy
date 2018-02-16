package uk.co.nickthecoder.fizzy.prop

interface PropListener<T> {

    /**
     * The property being listened to has changed. In the case of [PropCalculation], the value may not have
     * been re-evaluated yet, and therefore the next time [value] is referenced, it will cause a re-evaluation.
     * In the case of [PropValue], the new value will already be known.
     */
    fun dirty(prop: Prop<T>)

}
