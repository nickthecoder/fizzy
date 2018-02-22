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
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.PropConstant
import uk.co.nickthecoder.fizzy.prop.Shape1dPropType

class Shape1d(parent: Parent)
    : RealShape(parent) {

    override val context = CompoundEvaluationContext(listOf(
            constantsContext, ThisContext(PropConstant(this), Shape1dPropType.instance)))

    val start = Dimension2Expression("Dimension2(0mm,0mm)", context)

    val end = Dimension2Expression("Dimension2(1mm,1mm)", context)

    init {
        start.listeners.add(this)
        end.listeners.add(this)
    }
}
