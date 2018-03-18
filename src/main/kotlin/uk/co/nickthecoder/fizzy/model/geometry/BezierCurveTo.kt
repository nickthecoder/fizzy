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
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Prop

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
class BezierCurveTo(val a: Dimension2Expression, val b: Dimension2Expression, override val point: Dimension2Expression)
    : GeometryPart() {

    constructor(aFormula: String, bFormula: String, pointFormula: String) :
            this(Dimension2Expression(aFormula), Dimension2Expression(bFormula), Dimension2Expression(pointFormula))

    constructor(a: Dimension2, b: Dimension2, point: Dimension2) :
            this(a.toFormula(), b.toFormula(), point.toFormula())

    constructor() : this(Dimension2.ZERO_mm, Dimension2.ZERO_mm, Dimension2.ZERO_mm)

    val pointsCache = mutableListOf<Dimension2>()

    /**
     * When the prevPart changes, then we need to clear the pointsCache, and listen to the previous part's point
     * (and un-listen to the old version).
     */
    override var internalPrevPart: GeometryPart
        get() = super.internalPrevPart
        set(v) {
            if (super.internalPrevPart != this) {
                super.internalPrevPart.point.propListeners.remove(this)
            }
            super.internalPrevPart = v
            if (v != this) {
                v.point.propListeners.add(this)
            }
        }

    init {
        a.propListeners.add(this)
        b.propListeners.add(this)
        point.propListeners.add(this)
    }

    override fun setContext(context: EvaluationContext) {
        point.context = context
        a.context = context
        b.context = context
    }

    /**
     * When any of my data changes, or of the previous GeometryPart's point changes,
     * clear the pointsCache.
     */
    override fun dirty(prop: Prop<*>) {
        super.dirty(prop)
        pointsCache.clear()
    }

    override fun addMetaData(metaData: MetaData) {
        super.addMetaData(metaData)
        metaData.cells.add(MetaDataCell("A", a))
        metaData.cells.add(MetaDataCell("B", b))
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

    fun ensurePointsCache() {
        if (pointsCache.isEmpty()) {
            for (i in 1..16) {
                val t = i.toDouble() / 16.0
                pointsCache.add(pointAlong(prevPart.point.value, t))
            }
        }
    }

    override fun isCrossing(here: Dimension2, prev2: Dimension2): Boolean {
        ensurePointsCache()

        var result = false
        var prev = prevPart.point.value
        pointsCache.forEach { p ->
            if (LineTo.isCrossing(here, prev, p)) {
                result = !result
            }
            prev = p
        }
        return result
    }

    override fun isAlong(shape: Shape?, here: Dimension2, lineWidth: Dimension, minDistance: Dimension): Boolean {
        ensurePointsCache()

        var prev = prevPart.point.value
        pointsCache.forEach { p ->
            if (LineTo.isAlong(shape, here, prev, p, lineWidth, minDistance)) {
                return true
            }
            prev = p
        }
        return false
    }

    override fun checkAlong(shape: Shape, here: Dimension2): Pair<Double, Double>? {
        ensurePointsCache()

        var minDistance: Double = Double.MAX_VALUE
        var minAlong = 0.0
        var prev = prevPart.point.value
        val interval = 1.0 / (pointsCache.size)

        pointsCache.forEachIndexed { index, p ->
            LineTo.checkAlong(shape, here, prev, p)?.let { (dist, along) ->
                if (dist < minDistance) {
                    minDistance = dist
                    minAlong = interval * (index + along)
                }
            }
            prev = p
        }
        return minDistance to minAlong
    }

    override fun copy(link: Boolean): GeometryPart = BezierCurveTo(a.copy(link), b.copy(link), point.copy(link))

    override fun toString() = "BezierTo a=${a.value} b=${b.value} point=${point.value}"
}
