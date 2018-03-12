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
package uk.co.nickthecoder.fizzy.util

import junit.framework.TestCase
import uk.co.nickthecoder.fizzy.controller.CMouseEvent
import uk.co.nickthecoder.fizzy.controller.Controller
import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import uk.co.nickthecoder.fizzy.evaluator.Evaluator
import uk.co.nickthecoder.fizzy.model.*
import uk.co.nickthecoder.fizzy.model.geometry.GeometryPart
import uk.co.nickthecoder.fizzy.model.geometry.LineTo
import uk.co.nickthecoder.fizzy.model.geometry.MoveTo
import uk.co.nickthecoder.fizzy.prop.PropExpression
import java.lang.RuntimeException

abstract class MyTestCase : TestCase() {

    val tiny = 0.000001

    fun assertFails(action: () -> Any) {
        try {
            action()
        } catch (e: Exception) {
            return
        }
        throw RuntimeException("Expected an Exception")
    }

    fun assertFailsAt(position: Int, expression: () -> Any) {
        try {
            expression()
            throw RuntimeException("Expected an EvaluationException")
        } catch (e: EvaluationException) {
            if (position != e.index) {
                throw RuntimeException("Expected an EvaluationException at $position, but found one at ${e.index}", e)
            }
        }
    }

    fun dimension(formula: String) = Evaluator(formula).parse().value as Dimension

    fun dimension2(formula: String) = Evaluator(formula).parse().value as Dimension2


    fun testDouble(shape: Shape, exp: String): Double {
        return Evaluator(exp, shape.context).parse().value as Double
    }

    fun testBoolean(shape: Shape, exp: String): Boolean {
        return Evaluator(exp, shape.context).parse().value as Boolean
    }

    fun testPaint(shape: Shape, exp: String): Paint {
        return Evaluator(exp, shape.context).parse().value as Paint
    }


    fun createBox(parent: ShapeParent, size: String = "Dimension2(10mm,10mm)", at: String = "Dimension2(0mm,0mm)"): Shape2d {
        val box = Shape2d.create(parent)
        box.size.formula = size
        box.transform.pin.formula = at
        val geometry = box.geometry
        geometry.parts.add(MoveTo("Size * Vector2(0,0)"))
        geometry.parts.add(LineTo("Size * Vector2(1,0)"))
        geometry.parts.add(LineTo("Size * Vector2(1,1)"))
        geometry.parts.add(LineTo("Size * Vector2(0,1)"))
        geometry.parts.add(LineTo("Geometry1.Point1"))

        return box
    }

    fun createLine(parent: ShapeParent, start: String, end: String, lineWidth: String = "2mm"): Shape1d {
        val line = Shape1d.create(parent)

        line.start.formula = start
        line.end.formula = end
        line.lineWidth.formula = lineWidth

        val geometry = line.geometry
        geometry.parts.add(MoveTo("Dimension2(0mm,LineWidth/2)"))
        geometry.parts.add(LineTo("Dimension2(Length,LineWidth/2)"))

        return line
    }

    fun assertSamePoint(shapeA: Shape, a: GeometryPart, shapeB: Shape, b: GeometryPart) {
        val localA = shapeA.fromLocalToPage.value * a.point.value
        val localB = shapeB.fromLocalToPage.value * b.point.value
        TestCase.assertEquals(localA.x.inDefaultUnits, localB.x.inDefaultUnits, tiny)
        TestCase.assertEquals(localA.y.inDefaultUnits, localB.y.inDefaultUnits, tiny)
    }

    fun checkAllExpressions(shape: Shape) {
        shape.metaData().cells.forEach { metaDataCell ->
            val cp = metaDataCell.cellProp
            if (cp is PropExpression<*>) {
                val existingValue = cp.valueString()
                cp.forceRecalculation()
                val newValue = cp.valueString()
                if (existingValue != newValue) {
                    println("Inconsistant metadata : $metaDataCell")
                }
                kotlin.test.assertEquals(existingValue, newValue)
            }
        }
    }


    fun mouseEvent(pointFormula: String, button: Int = 0, isAdjust: Boolean = false, isConstrain: Boolean = false): CMouseEvent {
        val point = Evaluator(pointFormula).parse().value as Dimension2

        return CMouseEvent(point, button = button, isAdjust = isAdjust, isConstrain = isConstrain)
    }

    fun mouseEvent(point: Dimension2, button: Int = 0, isAdjust: Boolean = false, isConstrain: Boolean = false): CMouseEvent {
        return CMouseEvent(point, button = button, isAdjust = isAdjust, isConstrain = isConstrain)
    }

    fun drag(controller: Controller, fromFormula: String, toFormula: String, pauseHalfway: Boolean = true) {
        drag(controller, Evaluator(fromFormula).parse().value as Dimension2, Evaluator(toFormula).parse().value as Dimension2, pauseHalfway = pauseHalfway)
    }

    fun drag(controller: Controller, fromPoint: Dimension2, toPoint: Dimension2, pauseHalfway: Boolean = true) {
        val fromEvent = mouseEvent(fromPoint)
        controller.onMousePressed(fromEvent)
        controller.onDragDetected(fromEvent)
        if (pauseHalfway) {
            val midEvent = mouseEvent((fromPoint + toPoint) / 2.0)
            controller.onMouseDragged(midEvent)
        }
        val toEvent = mouseEvent(toPoint)
        controller.onMouseDragged(toEvent)
        controller.onMouseReleased(toEvent)
    }

}