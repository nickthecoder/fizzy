package uk.co.nickthecoder.fizzy.prop

import kotlin.reflect.KClass

abstract class PropType<T : Any>(val klass: KClass<*>) {

    abstract fun findField(prop: Prop<T>, name: String): Prop<*>?

    private fun findField2(prop: Prop<*>, name: String): Prop<*>? {
        @Suppress("UNCHECKED_CAST")
        return findField(prop as Prop<T>, name)
    }

    companion object {
        val propertyTypes = mutableMapOf<KClass<*>, PropType<*>>()

        fun field(prop: Prop<*>, fieldName: String): Prop<*>? {
            return propertyTypes[prop.value?.javaClass!!.kotlin]?.findField2(prop, fieldName)
        }

        fun put(propertyType: PropType<*>) {
            propertyTypes.put(propertyType.klass, propertyType)
        }

        init {
            put(AnglePropType())
            put(Dimension2PropType())
            put(DimensionPropType())
            put(DoublePropType())
            put(StringPropType())
            put(Vector2PropType())
        }
    }
}
