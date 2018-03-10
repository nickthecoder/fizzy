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
import uk.co.nickthecoder.fizzy.prop.PropCalculation2
import uk.co.nickthecoder.fizzy.prop.Shape1dPropType

class Shape1d private constructor(parent: ShapeParent)
    : Shape(parent) {

    override val context = createContext(ThisContext(this, Shape1dPropType.instance))

    override val transform = ShapeTransform(this)

    val start = Dimension2Expression("Dimension2(0mm,0mm)")

    val end = Dimension2Expression("Dimension2(1mm,1mm)")

    override val size = Dimension2Expression("Dimension2((End-Start).Length,LineWidth)")

    val length = PropCalculation2(start, end) { sv, ev -> (ev - sv).length() }

    init {
        listenTo(start)
        listenTo(end)
        listenTo(size)
        transform.locPin.formula = "Dimension2(Length*0.5, LineWidth * 0.5)"
        transform.pin.formula = "(Start+End) * 0.5"
        transform.rotation.formula = "(End-Start).Angle"
    }

    override fun copyInto(parent: ShapeParent, link: Boolean): Shape1d {
        val newShape = Shape1d(parent)
        newShape.postInit()
        populateShape(newShape, link)
        return newShape
    }

    override fun addMetaData(list: MutableList<MetaData>) {
        list.add(MetaData("Start", start))
        list.add(MetaData("End", end))
        super.addMetaData(list)
    }

    companion object {
        fun create(parent: ShapeParent): Shape1d {
            val result = Shape1d(parent)
            result.postInit()
            return result
        }
    }
}
