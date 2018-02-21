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
import uk.co.nickthecoder.fizzy.model.ShapeGroup
import kotlin.reflect.KClass


abstract class ShapePropType<T : Shape>(klass: KClass<T>) : PropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {
        return when (name) {
            "page" -> PropConstant(prop.value.page())
            "layer" -> PropConstant(prop.value.layer())
            else -> null
        }
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<T, *>? {
        return null
    }
}

class Shape1dPropType private constructor()
    : ShapePropType<Shape1d>(Shape1d::class) {

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
    : ShapePropType<Shape2d>(Shape2d::class) {

    override fun findField(prop: Prop<Shape2d>, name: String): Prop<*>? {
        return when (name) {
            "size" -> prop.value.size
            "position" -> prop.value.position
            "localPosition" -> prop.value.localPosition
            "rotation" -> prop.value.rotation
            "scale" -> prop.value.scale
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
        // TODO This won't work, because changes to the list won't make this "dirty"
            "children" -> PropCalculation1(prop) { prop.value.children }
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
