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

import uk.co.nickthecoder.fizzy.evaluator.CompoundEvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.*

class Shape1d private constructor(parent: Parent)
    : RealShape(parent) {

    override val context = CompoundEvaluationContext(listOf(
            constantsContext, ThisContext(PropConstant(this), Shape1dPropType.instance)))

    override val transform = ShapeTransform(this)

    val start = Dimension2Expression("Dimension2(0mm,0mm)", context)

    val end = Dimension2Expression("Dimension2(1mm,1mm)", context)

    val size = Dimension2Expression("Dimension2((End-Start).Length,LineWidth)", context)

    val length = PropCalculation2(start, end) { sv, ev -> (ev - sv).length() }

    val lineWidth = DimensionExpression("2mm", context)

    init {
        start.listeners.add(this)
        end.listeners.add(this)
        transform.locPin.expression = "Dimension2(Length*0.5, LineWidth * 0.5)"
        transform.pin.expression = "(Start+End) * 0.5"
        transform.rotation.expression = "(End-Start).Angle"
    }

    companion object {
        fun create(parent: Parent): Shape1d {
            val result = Shape1d(parent)
            result.postInit()
            return result
        }
    }
}
