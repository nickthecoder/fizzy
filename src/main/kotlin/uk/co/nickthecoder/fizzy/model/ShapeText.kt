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
import uk.co.nickthecoder.fizzy.prop.*
import uk.co.nickthecoder.fizzy.util.toFormula

class ShapeText private constructor(parent: ShapeParent)
    : Shape(parent) {

    override val context = createContext(ThisContext(this, ShapeTextPropType.instance))

    override val transform = ShapeTransform(this)

    override val size = Dimension2Expression("textSize()")

    val text = StringExpression("")

    val fontName = StringExpression("Sans Regular")

    val fontSize = DimensionExpression("20pt")

    val font = PropCalculation2(fontName, fontSize) {
        fontName, fontSize ->
        FFont.getFont(fontName, fontSize.inDefaultUnits)
    }

    /**
     * A value in the range 0..1. 0 => Left aligned, 1 => Right aligned.
     */
    val alignX = DoubleExpression("0.5")

    /**
     * A value in the range 0..1. 0 => Bottom Aligned, 1 => Top aligned.
     * Note, to align using the text's baseline, use the method: "baseline()"
     */
    val alignY = DoubleExpression("0.5")

    val clip = BooleanExpression(false)

    val marginTop = DimensionExpression("FontSize/2")
    val marginRight = DimensionExpression("FontSize/2")
    val marginBottom = DimensionExpression("FontSize/2")
    val marginLeft = DimensionExpression("FontSize/2")

    val multiLineText = PropCalculation4(text, font, alignX, alignY) {
        text, font, alignX, alignY ->
        MultiLineText(text, font, alignX, alignY)
    }

    init {
        listenTo(size, text, fontName, fontSize, alignX, alignY, clip, marginTop, marginRight, marginBottom, marginLeft)

        transform.locPin.formula = "Size * Vector2(AlignX,AlignY)"
    }

    override fun copyInto(parent: ShapeParent, link: Boolean): ShapeText {
        val newShape = ShapeText(parent)
        newShape.postInit()
        populateShape(newShape, link)
        return newShape
    }

    override fun addMetaData(metaData: MetaData) {
        super.addMetaData(metaData)
        metaData.cells.add(MetaDataCell("Text", text))
        metaData.cells.add(MetaDataCell("FontName", fontName))
        metaData.cells.add(MetaDataCell("FontSize", fontSize))
        metaData.cells.add(MetaDataCell("AlignX", alignX))
        metaData.cells.add(MetaDataCell("AlignY", alignY))
        metaData.cells.add(MetaDataCell("Clip", clip))

        metaData.cells.add(MetaDataCell("MarginTop", marginTop))
        metaData.cells.add(MetaDataCell("MarginRight", marginRight))
        metaData.cells.add(MetaDataCell("MarginBottom", marginBottom))
        metaData.cells.add(MetaDataCell("MarginLeft", marginLeft))
    }

    override fun isAt(pagePoint: Dimension2, minDistance: Dimension): Boolean {
        val relativePoint = transform.fromPageToLocal.value * pagePoint - transform.locPin.value
        if (multiLineText.value.isAt(relativePoint)) {
            return true
        }
        return super.isAt(pagePoint, minDistance)
    }

    /**
     * Returns the [alignY] value to align the text via the text's baseline.
     * It calculated how high up the baseline of the font it, in relation to the
     * height of the whole text.
     */
    fun baseline(): Double = 0.0 // TODO Implement

    /**
     * The size of the text
     */
    fun textSize(): Dimension2 {
        return multiLineText.value.size()
    }

    companion object {
        fun create(parent: ShapeParent): ShapeText {
            val result = ShapeText(parent)
            result.postInit()
            return result
        }

    }
}
