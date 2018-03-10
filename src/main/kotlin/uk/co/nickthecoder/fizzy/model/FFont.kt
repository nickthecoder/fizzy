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

interface FFont {

    val lineHeight: Dimension

    fun textSize(str: String): Dimension2


    companion object {

        fun getFont(fontName: String, fontSize: Double): FFont {
            return fontFactory(fontName, fontSize)
        }
    }
}

/**
 * The default fontFactory creates MockFFont objects.
 * If you have JavaFX in your classpath, replace this by :
 *
 * fontFactory = { fontName, fontSize -> JavaFXFFont( fontName, fontSize ) }
 */
var fontFactory: (String, Double) -> FFont = { _, fontSize -> MockFFont(fontSize) }

class MockFFont(val fontSize: Double) : FFont {

    override val lineHeight: Dimension = FONT_HEIGHT * fontSize

    override fun textSize(str: String): Dimension2 {
        val lines = str.split("\n")
        val maxWidth = lines.maxBy { it.length }?.length ?: 0
        return Dimension2(FONT_WIDTH * (fontSize * maxWidth), FONT_HEIGHT * (fontSize * lines.size))
    }

    companion object {
        val FONT_WIDTH = Dimension(10.0)
        val FONT_HEIGHT = Dimension(10.0)
    }
}
