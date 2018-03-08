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

import uk.co.nickthecoder.fizzy.model.*
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
            "ID" -> PropField("Shape.ID", prop) { prop.value.id }
            "Name" -> PropField("Shape.Name", prop) { prop.value.name }
            "Document" -> SimplePropField("Shape.Document", prop) { prop.value.document() }
            "Page" -> SimplePropField("Shape.Page", prop) { prop.value.page() }
            "Parent" -> SimplePropField("Shape.Parent", prop) { prop.value.parent }
            "Pin" -> PropField("Shape.Pin", prop) { prop.value.transform.pin }
            "LocPin" -> PropField("Shape.LocPin", prop) { prop.value.transform.locPin }
            "Scale" -> PropField("Shape.Scale", prop) { prop.value.transform.scale }
            "Rotation" -> PropField("Shape.Rotation", prop) { prop.value.transform.rotation }
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<in T>? {
        return when (name) {
            "findShape" -> FindShape(prop)
            else -> super.findMethod(prop, name)
        }
    }

}

abstract class RealShapePropType<T : RealShape>(klass: Class<T>)
    : ShapePropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {
        return when (name) {
            "Geometry" -> PropValue(prop.value.geometries)
            "ConnectionPoint" -> PropValue(prop.value.connectionPoints)
            "ControlPoint" -> PropValue(prop.value.controlPoints)
            "Scratch" -> PropValue(prop.value.scratches)
            "LineWidth" -> PropField("Shape.LineWidth", prop) { prop.value.lineWidth }
            "StrokeColor" -> PropField("Shape.StrokeColor", prop) { prop.value.strokeColor }
            "FillColor" -> PropField("Shape.FillColor", prop) { prop.value.fillColor }
            else -> return super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<in T>? {
        return when (name) {
            "connectTo" -> ConnectTo(prop)
            "connectAlong" -> ConnectAlong(prop)
            "findScratch" -> FindScratch(prop)
            else -> super.findMethod(prop, name)
        }
    }
}

class Shape1dPropType private constructor()
    : RealShapePropType<Shape1d>(Shape1d::class.java) {

    override fun findField(prop: Prop<Shape1d>, name: String): Prop<*>? {
        return when (name) {
            "Start" -> PropField("Shape.Start", prop) { prop.value.start }
            "End" -> PropField("Shape.End", prop) { prop.value.end }
            "Size" -> PropField("Shape.Size", prop) { prop.value.size }
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
    : RealShapePropType<Shape2d>(Shape2d::class.java) {

    override fun findField(prop: Prop<Shape2d>, name: String): Prop<*>? {
        return when (name) {
            "Size" -> prop.value.size
            else -> super.findField(prop, name)
        }
    }

    companion object {
        val instance = Shape2dPropType()
    }
}

class ShapeGroupPropType private constructor()
    : ShapePropType<ShapeGroup>(ShapeGroup::class.java) {

    companion object {
        val instance = ShapeGroupPropType()
    }
}
