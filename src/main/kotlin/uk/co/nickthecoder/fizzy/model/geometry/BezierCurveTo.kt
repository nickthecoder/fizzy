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
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.MetaData
import uk.co.nickthecoder.fizzy.model.MetaDataCell
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression

/**
 * Cubic bezier curves are defined by four points. The start and end points, plus two control points, which
 * the line usually heads towards, but donsn't touch.
 * The Start point is the [GeometryPart.point] of the previous geometry part in the list.
 * The end point is this.[point], and the two control points are [a] and [b].
 *
 * https://en.wikipedia.org/wiki/B%C3%A9zier_curve
 * A point along the curve is given by :
 *
 * B(t) = (1-t)³ P0 + 3(1-t)²t P1 + 3(1-t)t² P2 + t³ P3
 *
 * Where t is in the range 0..1. P0 is the start, P1 and P2 are the two control points, and P3 is the end point.
 *
 * https://pomax.github.io/bezierinfo/
 */
class BezierCurveTo(val a: Dimension2Expression, val b: Dimension2Expression, point: Dimension2Expression)
    : LineTo(point) {

    constructor(aFormula: String, bFormula: String, pointFormula: String) :
            this(Dimension2Expression(aFormula), Dimension2Expression(bFormula), Dimension2Expression(pointFormula))

    constructor(a: Dimension2, b: Dimension2, point: Dimension2) :
            this(a.toFormula(), b.toFormula(), point.toFormula())

    constructor() : this(Dimension2.ZERO_mm, Dimension2.ZERO_mm, Dimension2.ZERO_mm)

    init {
        a.propListeners.add(this)
        b.propListeners.add(this)
        point.propListeners.add(this)
    }

    override fun addMetaData(metaData: MetaData) {
        super.addMetaData(metaData)
        metaData.cells.add(MetaDataCell("A", a))
        metaData.cells.add(MetaDataCell("B", b))
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
        a.context = context
        b.context = context
    }

    /**
     * B(t) = (1-t)³ P0 + 3(1-t)²t P1 + 3(1-t)t² P2 + t³ P3
     *
     * If we use "along" as the value for t in the cubic bezier curve equation above,
     * then we can get a point along the curve.
     */
    override fun pointAlong(prev: Dimension2, along: Double): Dimension2 {
        val oneMinusT = 1 - along
        val oneMinusT2 = oneMinusT * oneMinusT
        val oneMinusT3 = oneMinusT2 * oneMinusT
        return prev * oneMinusT3 +
                a.value * (3 * oneMinusT2 * along) +
                b.value * (3 * oneMinusT * along * along) +
                point.value * (along * along * along)
    }

    override fun checkAlong(shape: Shape, here: Dimension2, prev: Dimension2): Pair<Double, Double>? {
        //TODO Implement
        return super.checkAlong(shape, here, prev)
    }

    override fun copy(link: Boolean): GeometryPart = BezierCurveTo(a.copy(link), b.copy(link), point.copy(link))

    override fun toString() = "BezierTo a=${a.value} b=${b.value} point=${point.value}"
}
