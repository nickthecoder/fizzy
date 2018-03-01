package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.model.RealShape
import uk.co.nickthecoder.fizzy.model.Scratch
import uk.co.nickthecoder.fizzy.prop.Prop

class FindScratch(prop: Prop<RealShape>)
    : TypedMethod1<RealShape, String>(prop, String::class) {

    override fun eval(a: String): Any {
        val scratch = prop.value.findScratch(a)
        setScratch(scratch)
        return scratch?.expression?.value ?: throw RuntimeException("Scratch $a not found")
    }

    var prevScratch: Scratch? = null

    /**
     * If the name of the scratch changes, then we need to become dirty, because the name may now refer to a
     * different Scratch (or the expression should throw, because the named Scratch no longer exists).
     */
    fun setScratch(scratch: Scratch?) {
        if (scratch !== prevScratch) {
            prevScratch?.let {
                unlistenTo(it.name)
                unlistenTo(it.expression)
            }
            scratch?.let {
                listenTo(scratch.name)
                listenTo(scratch.expression)
            }
        }
    }
}
