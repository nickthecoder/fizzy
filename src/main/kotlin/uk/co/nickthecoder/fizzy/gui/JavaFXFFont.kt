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
package uk.co.nickthecoder.fizzy.gui

import javafx.scene.text.Font
import javafx.scene.text.Text
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.FFont

class JavaFXFFont(fontName: String, fontSize: Double) : FFont {

    val fxFont = Font(fontName, fontSize)

    override val lineHeight: Dimension by lazy { (textSize("x\nx") - textSize("x")).y }

    override fun textSize(str: String): Dimension2 {
        val text = Text(str)

        text.font = fxFont
        val tb = text.boundsInLocal
        return Dimension2(Dimension(tb.width), Dimension(tb.height))

        //val stencil = Rectangle(tb.minX, tb.minY, tb.width, tb.height)
        //val intersection = javafx.scene.shape.Shape.intersect(text, stencil)
        //val ib = intersection.boundsInLocal

        //return Dimension2(Dimension(ib.width), Dimension(ib.height))
    }
}
