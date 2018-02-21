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

import uk.co.nickthecoder.fizzy.evaluator.CompoundContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.*

class Shape2d(parent: Parent)
    : Shape(parent) {

    override val context = CompoundContext(listOf(
            constantsContext, ThisContext(PropConstant(this), Shape2dPropType.instance)))

    /**
     * The position of this object relative to the parent (which is either a Group, or a Document).
     */
    val position = Dimension2Expression("Dimension2(0mm, 0mm)", context)

    /**
     * The local position of this object, which corresponds to the [position] within the parent.
     * It is also used as the center of rotation.
     * (0,0) would be the top-left of the shape, and [size] would be the bottom right.
     * The default value is [size] / 2, which is the center of the shape.
     */
    val localPosition = Dimension2Expression("size / 2", context)

    val size = Dimension2Expression("Dimension2(1mm,1mm)")

    // Should we have a scale? A scale would scale the line widths, the fonts etc
    val scale = Vector2Expression("Vector2(1, 1)", context)

    val rotation = AngleExpression("0 deg", context)

    init {
        position.listeners.add(this)
        localPosition.listeners.add(this)
        size.listeners.add(this)
        scale.listeners.add(this)
        rotation.listeners.add(this)
    }

}
