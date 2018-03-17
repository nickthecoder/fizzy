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

import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropExpression
import uk.co.nickthecoder.fizzy.prop.PropVariable

class RowFactory(val label: String, val create: (index: Int) -> Unit)

/**
 * Holds references to all [PropExpression]s within a [Shape] in a generic hierarchy. This is used when saving a
 * document, for debugging, and also within the unit tests.
 */
class MetaData(val type: String?) {

    val cells = mutableListOf<MetaDataCell>()

    val sections = mutableMapOf<String, MetaData>()

    val rows = mutableListOf<MetaData>()

    val rowFactories = mutableListOf<RowFactory>()

    var rowRemoval: ((Int) -> Unit)? = null

    fun isEmpty() = cells.isEmpty() && sections.isEmpty() && rows.isEmpty()

    fun newCell(cellName: String, expression: Prop<*>): MetaDataCell {
        val cell = MetaDataCell(cellName, expression)
        cells.add(cell)
        return cell
    }

    fun newSection(sectionName: String): MetaData {
        val section = MetaData(sectionName)
        sections[sectionName] = section
        return section
    }

    fun newRow(type: String?): MetaData {
        val row = MetaData(type)
        rows.add(row)
        return row
    }

    fun findCell(name: String): MetaDataCell? {
        cells.forEach { cell ->
            if (cell.cellName == name) {
                return cell
            }
        }
        return null
    }

    fun findCell(other: MetaDataCell) = findCell(other.cellName)

    fun copyInto(into: MetaData, link: Boolean) {
        cells.forEach { cell ->
            val intoCell = into.findCell(cell)
            if (intoCell == null) {
                throw IllegalStateException("Cell $cell not found")
            } else {
                val intoProp = intoCell.cellProp
                val fromProp = cell.cellProp

                if (intoProp is PropExpression<*> && fromProp is PropExpression<*>) {
                    intoProp.copyFrom(fromProp, link)
                } else if (intoProp is PropVariable && fromProp is PropVariable) {
                    @Suppress("UNCHECKED_CAST")
                    (intoProp as PropVariable<Any>).value = fromProp.value
                }

            }
        }

        sections.forEach { sectionName, section ->
            val intoSection = into.sections[sectionName] ?: throw IllegalStateException("Section name $sectionName not found")
            section.copyInto(intoSection, link)
        }

        rows.forEachIndexed { rowIndex, row ->
            if (rowIndex >= into.rows.size) throw IllegalStateException("Row $rowIndex not found")
            val intoRow = into.rows[rowIndex]
            row.copyInto(intoRow, link)
        }
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        buildString(buffer, "")
        return buffer.toString()
    }

    private fun buildString(buffer: StringBuffer, padding: String) {
        type?.let { buffer.append(it).append('\n') }

        cells.forEach { cell ->
            buffer.append(padding)
            buffer.append(cell)
            buffer.append('\n')
        }
        rows.forEachIndexed { rowIndex, row ->
            buffer.append(padding).append("Row $rowIndex { ")
            row.buildString(buffer, padding + "    ")
            buffer.append(padding).append("}\n")
        }
        sections.forEach { _, section ->
            buffer.append(padding).append("Section { ")
            section.buildString(buffer, padding + "    ")
            buffer.append(padding).append("}\n")
        }
    }


    fun buildExpressionMap(prefix: String): Map<PropExpression<*>, String> {
        val map = mutableMapOf<PropExpression<*>, String>()
        addToExpressionMap(map, prefix)
        return map
    }

    private fun addToExpressionMap(map: MutableMap<PropExpression<*>, String>, prefix: String) {
        cells.forEach { cell ->
            val exp = cell.cellProp
            if (exp is PropExpression<*>) {
                map[exp] = prefix + cell.cellName
            }
        }
        sections.forEach { name, section ->
            section.addToExpressionMap(map, prefix + name + ".")
        }

        rows.forEachIndexed { index, row ->
            row.addToExpressionMap(map, prefix + index + ".")
        }
    }
}
