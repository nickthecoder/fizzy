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
            "page" -> PropConstant(prop.value.page())
            "layer" -> PropConstant(prop.value.layer())
            else -> {
                val page = prop.value.page()

                super.findField(prop, name)
            }
        }
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<T, *>? {
        return null
    }
}

abstract class RealShapePropType<T : RealShape>(klass: KClass<in T>)
    : ShapePropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {
        if (name == "geometry") {
            return PropCalculation1(prop) { v -> v.geometries }
        }
        return super.findField(prop, name)
    }
}

class Shape1dPropType private constructor()
    : RealShapePropType<Shape1d>(Shape1d::class) {

    override fun findField(prop: Prop<Shape1d>, name: String): Prop<*>? {
        return when (name) {
            "start" -> prop.value.start
            "end" -> prop.value.end
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Shape1d>, name: String): PropMethod<Shape1d, *>? {
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
            "size" -> prop.value.transform.size
            "position" -> prop.value.transform.position
            "localPosition" -> prop.value.transform.localPosition
            "rotation" -> prop.value.transform.rotation
            "scale" -> prop.value.transform.scale
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<Shape2d>, name: String): PropMethod<Shape2d, *>? {
        return super.findMethod(prop, name)
    }

    companion object {
        val instance = Shape2dPropType()
    }
}

class ShapeGroupPropType private constructor()
    : ShapePropType<ShapeGroup>(ShapeGroup::class) {

    override fun findField(prop: Prop<ShapeGroup>, name: String): Prop<*>? {
        return when (name) {
            "children" -> FListProp(prop.value.children)
            else -> super.findField(prop, name)
        }
    }

    override fun findMethod(prop: Prop<ShapeGroup>, name: String): PropMethod<ShapeGroup, *>? {
        return super.findMethod(prop, name)
    }

    companion object {
        val instance = ShapeGroupPropType()
    }
}
