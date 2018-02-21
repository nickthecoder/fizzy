package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.model.Shape1d
import uk.co.nickthecoder.fizzy.model.Shape2d
import kotlin.reflect.KClass


abstract class ShapePropType<T : Shape>(klass: KClass<T>) : PropType<T>(klass) {

    override fun findField(prop: Prop<T>, name: String): Prop<*>? {
        return null
    }

    override fun findMethod(prop: Prop<T>, name: String): PropMethod<T, *>? {
        return null
    }
}

class Shape1dPropType : ShapePropType<Shape1d>(Shape1d::class) {

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
}

class Shape2dPropType : ShapePropType<Shape2d>(Shape2d::class) {

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
}
