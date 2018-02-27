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
import kotlin.reflect.KClass


abstract class ShapePropType<T : Shape>(klass: KClass<in T>)
    : PropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {
        return when (name) {
            "Document" -> PropConstant(prop.value.document())
            "Page" -> PropConstant(prop.value.page())
            "Parent" -> PropConstant(prop.value.parent)
            "Pin" -> prop.value.transform.pin
            "LocPin" -> prop.value.transform.locPin
            "Scale" -> prop.value.transform.scale
            "Rotation" -> prop.value.transform.rotation
            else -> {
                // Allow the name of any shape on the document to be addresses as a top-level item
                val document = prop.value.document()
                val shape = document.findShape(name)
                if (shape != null) {
                    return PropConstant(shape)
                }
                super.findField(prop, name)
            }
        }
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<T>? {
        return null
    }
}

abstract class RealShapePropType<T : RealShape>(klass: KClass<in T>)
    : ShapePropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {
        return when (name) {
            "Geometry" -> PropCalculation1(prop) { v -> v.geometries }
            "LineWidth" -> prop.value.lineWidth
            else -> return super.findField(prop, name)
        }
    }
}

class Shape1dPropType private constructor()
    : RealShapePropType<Shape1d>(Shape1d::class) {

    override fun findField(prop: Prop<Shape1d>, name: String): Prop<*>? {
        return when (name) {
            "Start" -> prop.value.start
            "End" -> prop.value.end
            "Size" -> prop.value.size
            "Length" -> prop.value.length
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Shape1d>, name: String): PropMethod<Shape1d>? {
        return super.findMethod(prop, name)
    }

    companion object {
        val instance = Shape1dPropType()
    }
}

class Shape2dPropType private constructor()
    : RealShapePropType<Shape2d>(Shape2d::class) {

    override fun findField(prop: Prop<Shape2d>, name: String): Prop<*>? {
        return when (name) {
            "Size" -> prop.value.size
            "Pin" -> prop.value.transform.pin
            "LocPin" -> prop.value.transform.locPin
            "Rotation" -> prop.value.transform.rotation
            "Scale" -> prop.value.transform.scale
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Shape2d>, name: String): PropMethod<Shape2d>? {
        return super.findMethod(prop, name)
    }

    companion object {
        val instance = Shape2dPropType()
    }
}

class ShapeGroupPropType private constructor()
    : ShapePropType<ShapeGroup>(ShapeGroup::class) {

    companion object {
        val instance = ShapeGroupPropType()
    }
}
