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
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression


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

    override fun copy(link: Boolean): GeometryPart = BezierCurveTo(a.copy(link), b.copy(link), point.copy(link))

    override fun toString() = "BezierTo a=${a.value} b=${b.value} point=${point.value}"
}
