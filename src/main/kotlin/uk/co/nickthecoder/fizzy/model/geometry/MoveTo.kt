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
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression


class MoveTo(override val point: Dimension2Expression)

    : GeometryPart() {

    constructor(pointFormula: String) : this(Dimension2Expression(pointFormula))

    constructor(point: Dimension2) : this(point.toFormula())

    constructor() : this(Dimension2.ZERO_mm)

    init {
        point.propListeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
    }

    override fun isAlong(shape: Shape?, here: Dimension2, lineWidth: Dimension, minDistance: Dimension) = false

    override fun checkAlong(shape: Shape, here: Dimension2): Pair<Double, Double>? = null

    override fun isCrossing(here: Dimension2) = false

    override fun pointAlong(along: Double) = Dimension2.ZERO_mm

    override fun copy(link: Boolean): GeometryPart = MoveTo(point.copy(link))

    override fun toString() = "MoveTo point=${point.value}"
}
