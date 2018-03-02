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
package uk.co.nickthecoder.fizzy.model.geometry

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression


class MoveTo(formula: String = "Dimension2(0mm, 0mm)")
    : GeometryPart() {

    override val point = Dimension2Expression(formula)

    init {
        point.propListeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }

    override fun formula() = "LineTo point='${point.formula}'"

    override fun isAlong(here: Dimension2, prev: Dimension2, thickness: Dimension) = false

    override fun isCrossing(here: Dimension2, prev: Dimension2) = false

    override fun pointAlong(prev: Dimension2, along: Double) = Dimension2.ZERO_mm

    override fun copy(): GeometryPart = MoveTo(point.formula)

    override fun toString() = "MoveTo point=${point.value}"
}
