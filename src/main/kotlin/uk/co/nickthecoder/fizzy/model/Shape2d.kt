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

import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Shape2dPropType

class Shape2d private constructor(parent: ShapeParent)
    : RealShape(parent) {

    override val context = createContext(ThisContext(this, Shape2dPropType.instance))

    override val transform = ShapeTransform(this)

    override val size = Dimension2Expression("Dimension2(1mm,1mm)")


    init {
        size.propListeners.add(this)
    }

    override fun copyInto(parent: ShapeParent, link: Boolean): Shape2d {
        val newShape = Shape2d(parent)
        newShape.postInit()
        populateShape(newShape, link)
        return newShape
    }

    companion object {
        fun create(parent: ShapeParent): Shape2d {
            val result = Shape2d(parent)
            result.postInit()
            return result
        }
    }
}
