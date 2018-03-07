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
package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.EvaluationContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Color
import uk.co.nickthecoder.fizzy.model.Paint

class ColorPropType private constructor()

    : PropType<Color>(Color::class) {

    override fun findField(prop: Prop<Color>, name: String): Prop<*>? {
        return when (name) {
            "Red" -> SimplePropField("Color.Red", prop) { it.value.red }
            "Green" -> SimplePropField("Color.Green", prop) { it.value.green }
            "Blue" -> SimplePropField("Color.Blue", prop) { it.value.blue }
            "Opacity" -> SimplePropField("Color.Opacity", prop) { it.value.opacity }
            "Alpha" -> SimplePropField("Color.Alpha", prop) { it.value.opacity }
            "Hue" -> SimplePropField("Color.Hue", prop) { prop.value.hue }
            "Saturation" -> SimplePropField("Color.Brightness", prop) { it.value.saturation }
            "Brightness" -> SimplePropField("Color.Brightness", prop) { it.value.brightness }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Color>, name: String): PropMethod<in Color>? {
        return when (name) {
            "brighter" -> PropMethod0(prop) { prop.value.brighter() }
            "darker" -> PropMethod0(prop) { prop.value.darker() }
            "saturate" -> PropMethod0(prop) { prop.value.saturate() }
            "desaturate" -> PropMethod0(prop) { prop.value.desaturate() }
            "grayscale" -> PropMethod0(prop) { prop.value.grayscale() }
            "invert" -> PropMethod0(prop) { prop.value.invert() }
            "isOpaque" -> PropMethod0(prop) { prop.value.isOpaque() }
            "interpolate" -> PropMethod2(prop, Color::class, Double::class) { other, t -> prop.value.interpolate(other, t) }
            else -> return super.findMethod(prop, name)
        }
    }

    companion object {
        val instance = ColorPropType()
    }

}

class PaintExpression
    : PropExpression<Paint> {

    override val defaultValue = Color.BLACK

    constructor(expression: String, context: EvaluationContext = constantsContext) : super(expression, Paint::class, context)

    constructor(other: PaintExpression) : super(other, Paint::class)

    override fun constant(value: Paint) {
        formula = value.toFormula()
    }

    override fun copy(link: Boolean) = if (link) PaintExpression(this) else PaintExpression(formula)

    override fun valueString(): String {
        val v = value
        return when (v) {
            is Color -> v.toFormula()
            else -> v.toString()
        }
    }
}
