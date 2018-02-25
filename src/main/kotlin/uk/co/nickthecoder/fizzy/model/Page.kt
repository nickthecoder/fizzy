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
package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.prop.PropConstant

class Page(val document: Document)
    : Parent, HasChangeListeners<Page> {

    override val listeners = ChangeListeners<Page>()

    override val children = MutableFList<Shape>()

    private val shapesListener = ChangeAndCollectionListener(this, children)

    init {
        document.pages.add(this)
    }

    override fun document() = document

    override fun page() = this

    override val transformation = PropConstant(Matrix33.identity)

    fun findShape(id: String): Shape? {
        children.forEach { shape ->
            val found = shape.findShape(id)
            if (found != null) return found
        }
        return null
    }
}
