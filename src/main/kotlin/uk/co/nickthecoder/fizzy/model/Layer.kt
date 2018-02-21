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

class Layer(val page: Page)
    : Parent, HasChangeListeners<Layer> {

    override val listeners = ChangeListeners<Layer>()

    override var children = MutableFList<Shape>()

    private val shapesListener = ChangeAndCollectionListener(this, children)

    init {
        page.layers.add(this)
    }

    override fun page() = page

    override fun layer() = this

}
