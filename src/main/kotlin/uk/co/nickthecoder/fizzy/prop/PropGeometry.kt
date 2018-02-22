package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Geometry
import uk.co.nickthecoder.fizzy.model.GeometryPart
import uk.co.nickthecoder.fizzy.model.LineTo
import uk.co.nickthecoder.fizzy.model.MoveTo


class GeometryPropType private constructor()
    : PropType<Geometry>(Geometry::class) {

    override fun findField(prop: Prop<Geometry>, name: String): Prop<*>? {
        return when (name) {
            "lineWidth" -> prop.value.lineWidth
            else -> {
                // Allow access to any of the Geometries parts, without the hassle of ".parts.xxx"
                val partsProp = FListProp<GeometryPart>(prop.value.parts)
                val found = partsProp.field(name)
                found ?: super.findField(prop, name)
            }
        }
    }

    companion object {
        val instance = GeometryPropType()
    }
}

class MoveToPropType private constructor()
    : PropType<MoveTo>(MoveTo::class) {

    override fun findField(prop: Prop<MoveTo>, name: String): Prop<*>? {
        return when (name) {
            "point" -> prop.value.point
            else -> null
        }
    }

    companion object {
        val instance = MoveToPropType()
    }
}

class LineToPropType private constructor()
    : PropType<LineTo>(LineTo::class) {

    override fun findField(prop: Prop<LineTo>, name: String): Prop<*>? {
        return when (name) {
            "point" -> prop.value.point
            else -> null
        }
    }

    companion object {
        val instance = LineToPropType()
    }
}
