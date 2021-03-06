/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.prop.methods.FindShape

class PagePropType private constructor()
    : PropType<Page>(Page::class.java) {

    override fun findField(prop: Prop<Page>, name: String): Prop<*>? {
        if (name.startsWith("Shape")) {
            try {
                val id = name.substring(5).toInt()
                return SimplePropField("Page.Shape", prop) { it.value.findShape(id) ?: throw RuntimeException("Shape $id not found") }
            } catch (e: NumberFormatException) {
                // Do nothing
            }
        }
        return super.findField(prop, name)
    }

    override fun findMethod(prop: Prop<Page>, name: String): PropMethod<in Page>? {
        return when (name) {
            "Shape" -> FindShape(prop)
            else -> return super.findMethod(prop, name)
        }
    }

    companion object {
        val instance = PagePropType()
    }
}
