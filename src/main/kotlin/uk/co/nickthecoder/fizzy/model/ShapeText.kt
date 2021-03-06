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

class ShapeText private constructor(parent: ShapeParent, linkedFrom: Shape?, id: Int = parent.document().generateShapeId())
    : Shape(parent, linkedFrom, id) {

    override val context = createContext(ThisContext(this, ShapeTextPropType.instance))

    override val transform = ShapeTransform(this)

    override val locks = Locks(this, context)

    override val size = Dimension2Expression("textSize()")

    val text = StringExpression("".toFormula())

    val fontName = StringExpression("Sans Regular".toFormula())

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

        strokeColor.formula = Color.TRANSPARENT.toFormula()
        transform.locPin.formula = "Size * Vector2(AlignX,AlignY)"
    }

    override fun copyInto(parent: ShapeParent, link: Boolean): ShapeText {
        val newShape = ShapeText(parent, if (link) this else null)
        newShape.postInit()
        populateShape(newShape, link)
        return newShape
    }

    override fun addMetaData(metaData: MetaData) {
        super.addMetaData(metaData)
        metaData.newCell("Text", text)
        metaData.newCell("FontName", fontName)
        metaData.newCell("FontSize", fontSize)
        metaData.newCell("AlignX", alignX)
        metaData.newCell("AlignY", alignY)
        metaData.newCell("Clip", clip)

        metaData.newCell("MarginTop", marginTop)
        metaData.newCell("MarginRight", marginRight)
        metaData.newCell("MarginBottom", marginBottom)
        metaData.newCell("MarginLeft", marginLeft)
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
            val result = ShapeText(parent, null)
            result.postInit()
            return result
        }

        internal fun create(parent: ShapeParent, linkedFrom: Shape?, id: Int): ShapeText {
            val result = ShapeText(parent, linkedFrom, id)
            result.postInit()
            return result
        }

    }
}
