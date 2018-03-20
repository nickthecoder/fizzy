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

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.prop.BooleanExpression

class Locks(val shape: Shape, context: EvaluationContext)
    : MetaDataAware {

    val size = BooleanExpression(false, context)

    val rotation = BooleanExpression(false, context)

    override fun metaData(): MetaData {
        val metaData = MetaData(null)
        addMetaData(metaData)
        return metaData
    }

    fun addMetaData(metaData: MetaData) {
        val section = metaData.newSection("Lock")
        if (shape is Shape2d) {
            section.newCell("Size", size)
            section.newCell("Rotation", rotation)
        }
    }

    init {
        shape.listenTo(size, rotation)
    }

}
