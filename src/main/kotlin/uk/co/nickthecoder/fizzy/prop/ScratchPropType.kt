package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.collection.CollectionListener
import uk.co.nickthecoder.fizzy.collection.FCollection
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.model.Scratch
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners


class ScratchProp(scratch: Scratch)
    : PropValue<Scratch>(scratch),
        PropListener,
        HasChangeListeners<ScratchProp> {

    override val changeListeners = ChangeListeners<ScratchProp>()

    init {
        scratch.name.propListeners.add(this)
        scratch.expression.propListeners.add(this)
    }

    /**
     * Any changes to Scratch's data causes this [Prop]'s propListeners to be notified.
     * The [ScratchProp]'s constructor adds itself to the listeners of each of [Scratch]'s [Prop]s.
     */
    override fun dirty(prop: Prop<*>) {
        propListeners.fireDirty(this)
    }
}

class ScratchList : MutableFList<ScratchProp>()

class ScratchListPropType private constructor()
    : PropType<ScratchList>(ScratchList::class) {

    override fun findField(prop: Prop<ScratchList>, name: String): Prop<*>? {
        // Lets us access a scratch value using : Scratch.TheScratchName
        prop.value.forEach {
            val scratch = it.value
            if (scratch.name.value == name) {
                return FindScratchField(prop.value, name)
            }
        }
        return super.findField(prop, name)
    }

    companion object {
        val instance = ScratchListPropType()
    }
}

class FindScratchField(val scratchList: ScratchList, val name: String)
    : PropCalculation<Any>(), CollectionListener<ScratchProp> {

    /**
     * If the scratch is removed, we need to become dirty!
     */
    override fun removed(collection: FCollection<ScratchProp>, item: ScratchProp) {
        if (item.value.name.value == name) {
            dirty = true
        }
    }

    /**
     * If the name has been added back again, become dirty (probably not needed, but it won't hurt).
     */
    override fun added(collection: FCollection<ScratchProp>, item: ScratchProp) {
        if (item.value.name.value == name) {
            dirty = true
        }
    }

    /**
     * Listen to the name, so that if it changes, we become dirty (and will throw when re-evaluated).
     */
    var scratchNameProp: Prop<String>? = null
        set(v) {
            if (field != v) {
                field?.let { unlistenTo(it) }
                v?.let { listenTo(it) }
                field = v
            }
        }

    override fun eval(): Any {
        scratchList.forEach {
            val scratch = it.value
            if (scratch.name.value == name) {
                scratchNameProp = scratch.name
                return scratch.expression.value
            }
        }
        throw RuntimeException("Scratch $name not found")
    }

}