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
package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.ShapeParent
import uk.co.nickthecoder.fizzy.prop.Prop

class FindShape(prop: Prop<ShapeParent>)
    : TypedMethod1<ShapeParent, String>(prop, String::class.java) {

    override fun eval(a: String): Any {
        val shape = prop.value.findShape(a)
        setShape(shape)
        return shape ?: throw RuntimeException("Shape $a not found")
    }

    var prevShape: Shape? = null

    /**
     * If the name changes, then we need to become dirty, because the name may now refer to a
     * different Shape (or the expression should throw, because the named Shape no longer exists).
     */
    fun setShape(shape: Shape?) {
        if (shape !== prevShape) {
            prevShape?.let {
                unlistenTo(it.name)
            }
            shape?.let {
                listenTo(shape.name)
            }
        }
    }
}
