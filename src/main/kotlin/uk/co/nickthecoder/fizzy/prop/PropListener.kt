package uk.co.nickthecoder.fizzy.prop

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

interface PropListener<T> {
    fun changed(prop: Prop<T>)
}
