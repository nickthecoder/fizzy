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

import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.Shape2d
import uk.co.nickthecoder.fizzy.model.ShapeText
import uk.co.nickthecoder.fizzy.prop.methods.ConnectAlong
import uk.co.nickthecoder.fizzy.prop.methods.ConnectTo
import uk.co.nickthecoder.fizzy.prop.methods.FindScratch
import uk.co.nickthecoder.fizzy.prop.methods.FindShape

abstract class ShapePropType<T : Shape>(klass: Class<T>)
    : PropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {

        if (name.startsWith("Shape")) {
            try {
                val id = name.substring(5).toInt()
                val shape = prop.value.findShape(id) ?: throw RuntimeException("Shape $id not found")
                return PropValue(shape)
            } catch (e: NumberFormatException) {
                // Do nothing
            }
        }

        return when (name) {
            "ID" -> SimplePropField("Shape.ID", prop) { prop.value.id }
            "Name" -> PropField("Shape.Name", prop) { prop.value.name }
            "Document" -> SimplePropField("Shape.Document", prop) { prop.value.document() }
            "Page" -> SimplePropField("Shape.Page", prop) { prop.value.page() }
            "Parent" -> SimplePropField("Shape.Parent", prop) { prop.value.parent }
            "Pin" -> PropField("Shape.Pin", prop) { prop.value.transform.pin }
            "LocPin" -> PropField("Shape.LocPin", prop) { prop.value.transform.locPin }
            "Scale" -> PropField("Shape.Scale", prop) { prop.value.transform.scale }
            "Rotation" -> PropField("Shape.Rotation", prop) { prop.value.transform.rotation }

            "Geometry" -> PropValue(prop.value.geometries)
            "ConnectionPoint" -> PropValue(prop.value.connectionPoints)
            "ControlPoint" -> PropValue(prop.value.controlPoints)
            "Scratch" -> PropValue(prop.value.scratches)
            "LineWidth" -> PropField("Shape.LineWidth", prop) { prop.value.lineWidth }
            "StrokeColor" -> PropField("Shape.StrokeColor", prop) { prop.value.strokeColor }
            "FillColor" -> PropField("Shape.FillColor", prop) { prop.value.fillColor }
            "StrokeCap" -> PropField("Shape.StrokeCap", prop) { prop.value.strokeCap }
            "StrokeJoin" -> PropField("Shape.StrokeJoin", prop) { prop.value.strokeJoin }
            "Size" -> PropField("Shape.Size", prop) { prop.value.size }

            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<in T>? {
        return when (name) {
            "findShape" -> FindShape(prop)
            "connectTo" -> ConnectTo(prop)
            "connectAlong" -> ConnectAlong(prop)
            "findScratch" -> FindScratch(prop)
            else -> super.findMethod(prop, name)
        }
    }

}

class Shape1dPropType private constructor()
    : ShapePropType<Shape1d>(Shape1d::class.java) {

    override fun findField(prop: Prop<Shape1d>, name: String): Prop<*>? {
        return when (name) {
            "Start" -> PropField("Shape.Start", prop) { prop.value.start }
            "End" -> PropField("Shape.End", prop) { prop.value.end }
            "Length" -> PropField("Shape.Length", prop) { prop.value.length }
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Shape1d>, name: String): PropMethod<in Shape1d>? {
        return super.findMethod(prop, name)
    }

    companion object {
        val instance = Shape1dPropType()
    }
}

class Shape2dPropType private constructor()
    : ShapePropType<Shape2d>(Shape2d::class.java) {

    companion object {
        val instance = Shape2dPropType()
    }
}


class ShapeTextPropType private constructor()
    : ShapePropType<ShapeText>(ShapeText::class.java) {

    override fun findField(prop: Prop<ShapeText>, name: String): Prop<*>? {
        return when (name) {
            "Text" -> PropField("Shape.Text", prop) { prop.value.text }
            "FontSize" -> PropField("Shape.Text", prop) { prop.value.fontSize }
            "AlignX" -> PropField("Shape.AlignX", prop) { prop.value.alignX }
            "AlignY" -> PropField("Shape.AlignY", prop) { prop.value.alignY }
            "Clip" -> PropField("Shape.Clip", prop) { prop.value.clip }

            "MarginTop" -> PropField("Shape.MarginTop", prop) { prop.value.marginTop }
            "MarginRight" -> PropField("Shape.MarginTop", prop) { prop.value.marginRight }
            "MarginBottom" -> PropField("Shape.MarginTop", prop) { prop.value.marginBottom }
            "MarginLeft" -> PropField("Shape.MarginTop", prop) { prop.value.marginLeft }
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<ShapeText>, name: String): PropMethod<in ShapeText>? {
        return when (name) {
            "baseline" -> PropMethod0(prop) { prop.value.baseline() }
            "textSize" -> PropMethod0(prop) { prop.value.textSize() }
            else -> super.findMethod(prop, name)
        }
    }

    companion object {
        val instance = ShapeTextPropType()
    }
}
