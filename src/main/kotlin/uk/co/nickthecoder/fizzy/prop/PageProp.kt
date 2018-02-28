package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Page

class PagePropType private constructor()
    : PropType<Page>(Page::class) {

    override fun findField(prop: Prop<Page>, name: String): Prop<*>? {
        if (name.startsWith("Shape")) {
            try {
                val id = name.substring(5).toInt()
                val shape = prop.value.findShape(id) ?: throw RuntimeException("Shape $id not found")
                return PropConstant(shape)
            } catch (e: NumberFormatException) {
                // Do nothing
            }
        }
        return super.findField(prop, name)
    }

    companion object {
        val instance = PagePropType()
    }
}
