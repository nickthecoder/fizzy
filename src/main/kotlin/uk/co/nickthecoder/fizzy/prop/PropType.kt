package uk.co.nickthecoder.fizzy.prop

import kotlin.reflect.KClass

abstract class PropType<T : Any>(val klass: KClass<*>) {

    abstract fun findField(prop: Prop<T>, name: String): PropField<T, *>?

    private fun findField2(prop: Prop<*>, name: String): PropField<T, *>? {
        @Suppress("UNCHECKED_CAST")
        return findField(prop as Prop<T>, name)
    }

    abstract fun findMethod(prop: Prop<T>, name: String): PropMethod<T, *>?

    private fun findMethod2(prop: Prop<*>, name: String): PropMethod<T, *>? {
        @Suppress("UNCHECKED_CAST")
        return findMethod(prop as Prop<T>, name)
    }

    companion object {
        val propertyTypes = mutableMapOf<KClass<*>, PropType<*>>()

        fun field(prop: Prop<*>, fieldName: String): PropField<*, *>? {
            return propertyTypes[prop.value?.javaClass!!.kotlin]?.findField2(prop, fieldName)
        }

        fun method(prop: Prop<*>, methodName: String): PropMethod<*, *>? {
            return propertyTypes[prop.value?.javaClass!!.kotlin]?.findMethod2(prop, methodName)
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
