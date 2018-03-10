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

/**
 * Given a multi-line piece a text, splits it up into individual lines, such that each line knows its offset and width.
 * The values dx and dy are relative to the "locPin" of the ShapeText.
 */
class MultiLineText(text: String, font: FFont, val alignX: Double, val alignY: Double) {

    val lines = mutableListOf<SingleLineText>()

    val lineHeight = font.lineHeight

    val maxWidth: Dimension

    init {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            val dy = font.lineHeight * index.toDouble() - font.lineHeight * lines.size.toDouble() * alignY
            this.lines.add(SingleLineText(font, line, dy))
        }
        maxWidth = this.lines.maxBy { it.width }?.width ?: Dimension.ZERO_mm
        this.lines.forEach { line ->
            line.dx = -line.width * alignX
        }
    }

    /**
     * Is the point within the rectangle for one of the lines of text?
     */
    fun isAt(point: Dimension2): Boolean {
        val lineIndex = Math.floor((point.y.inDefaultUnits + (lines.size * lineHeight.inDefaultUnits) * alignY) / lineHeight.inDefaultUnits).toInt()
        if (lineIndex < 0 || lineIndex >= lines.size) return false

        return lines[lineIndex].isAt(point.x)
    }

    fun size(): Dimension2 = Dimension2(maxWidth, lineHeight * lines.size.toDouble())

    inner class SingleLineText(font: FFont, val text: String, val dy: Dimension) {
        val width = font.textSize(text).x
        val height = lineHeight

        lateinit var dx: Dimension
        fun isAt(x: Dimension): Boolean {
            val tx = x.inDefaultUnits - dx.inDefaultUnits
            return tx > 0 && tx < width.inDefaultUnits
        }
    }


}
