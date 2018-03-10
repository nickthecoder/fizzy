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
package uk.co.nickthecoder.fizzy.view

import uk.co.nickthecoder.fizzy.model.*

/**
 * Converts each drawing primitive into ones using absolute coordinates, rather than the local
 * coordinates. This is achieved by maintaining a transformation matrix, which is adjusted by each
 * of [translate], [rotate], [scale].
 *
 * Note that line widths, and text won't work correctly if x an y have been scaled differently.
 *
 * This was primarily defined for use with a mock [DrawContext] used within the unit tests,
 * but it may have some other uses, so I've placed in in the main module.
 */
abstract class AbsoluteContext(scale: Double = 1.0) : DrawContext {

    var state = State(Matrix33.scale(scale, scale))

    val stateStack = mutableListOf<State>()

    fun transform(point: Dimension2): Vector2 {
        // println( "AbsoluteContext : Transforming point ${point} to ${state.transformation.times(point.x.inDefaultUnits, point.y.inDefaultUnits)}")
        return state.transformation.times(point.x.inDefaultUnits, point.y.inDefaultUnits)
    }

    fun transform(x: Double, y: Double): Vector2 {
        return state.transformation.times(x, y)
    }

    override fun save() {
        stateStack.add(state)
    }

    override fun restore() {
        state = stateStack.removeAt(stateStack.size - 1)
    }


    override fun translate(by: Dimension2) {
        translate(by.x, by.y)
    }

    override fun translate(x: Dimension, y: Dimension) {
        state.transformation = state.transformation * Matrix33.translate(x.inDefaultUnits, y.inDefaultUnits)
    }

    override fun rotate(by: Angle) {
        state.transformation = state.transformation * Matrix33.rotate(by)
    }

    override fun scale(by: Vector2) {
        state.transformation = state.transformation * Matrix33.scale(by.x, by.y)
    }

    override fun scale(by: Double) {
        state.transformation = state.transformation * Matrix33.scale(by, by)
    }

    /**
     * Note. this doesn't work correctly if the x and y have been scaled differently!
     */
    override fun lineWidth(width: Dimension) {
        state.lineWidth = transform(Dimension2(width, width)).length()
    }


    override fun moveTo(point: Dimension2) {
        absoluteMoveTo(transform(point))
    }

    override fun moveTo(x: Double, y: Double) {
        absoluteMoveTo(transform(x, y))
    }

    override fun lineTo(point: Dimension2) {
        absoluteLineTo(transform(point))
    }

    override fun lineTo(x: Double, y: Double) {
        absoluteLineTo(transform(x, y))
    }

    override fun multiLineText(multiLineText: MultiLineText, stroke: Boolean, fill: Boolean) {
        multiLineText.lines.forEach { line ->
            absoluteText(transform(line.dx.inDefaultUnits, line.dy.inDefaultUnits), line.text, stroke, fill)
        }
    }


    abstract fun absoluteMoveTo(point: Vector2)

    abstract fun absoluteLineTo(point: Vector2)

    abstract fun absoluteText(point: Vector2, str: String, stroke: Boolean, fill: Boolean)


    class State(var transformation: Matrix33, var lineWidth: Double = 1.0)
}
