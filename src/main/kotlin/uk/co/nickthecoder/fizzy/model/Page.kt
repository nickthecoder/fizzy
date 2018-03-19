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
import uk.co.nickthecoder.fizzy.util.ChangeAndListListener
import uk.co.nickthecoder.fizzy.util.ChangeListeners
import uk.co.nickthecoder.fizzy.util.HasChangeListeners

class Page internal constructor(val document: Document, add: Boolean)
    : ShapeParent, HasChangeListeners<Page> {

    constructor(document: Document) : this(document, true)

    override val changeListeners = ChangeListeners<Page>()

    override val children = MutableFList<Shape>()

    private val childrenListener = ChangeAndListListener(this, children,
            onAdded = { item, _ -> if (item.page() != this@Page) throw IllegalStateException("Added $item to a different page") }
    )

    init {
        if (add) {
            document.pages.add(this)
        }
    }

    override fun document() = document

    override fun page() = this

    override val fromLocalToParent = PropConstant(Matrix33.identity)
    override val fromParentToLocal = PropConstant(Matrix33.identity)
    override val fromLocalToPage = PropConstant(Matrix33.identity)
    override val fromPageToLocal = PropConstant(Matrix33.identity)

    override fun findShape(name: String): Shape? {
        children.forEach { shape ->
            val found = shape.findShape(name)
            if (found != null) return found
        }
        return null
    }

    fun findShape(id: Int): Shape? {
        children.forEach { shape ->
            val found = shape.findShape(id)
            if (found != null) return found
        }
        return null
    }

    fun findShapeAt(pagePoint: Dimension2, minDistance: Dimension): Shape? {
        children.forEach { shape ->
            if (shape.isAt(pagePoint, minDistance)) {
                return shape
            }
        }
        return null
    }



}
