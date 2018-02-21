package uk.co.nickthecoder.fizzy.prop

import kotlin.reflect.KClass

abstract class PropType<T : Any>(val klass: KClass<*>) {

    abstract fun findField(prop: Prop<T>, name: String): Prop<*>?

    private fun findField2(prop: Prop<*>, name: String): Prop<*>? {
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

        fun field(prop: Prop<*>, fieldName: String): Prop<*>? {
            return propertyTypes[prop.value?.javaClass!!.kotlin]?.findField2(prop, fieldName)
        }

        fun method(prop: Prop<*>, methodName: String): PropMethod<*, *>? {
            return propertyTypes[prop.value?.javaClass!!.kotlin]?.findMethod2(prop, methodName)
        }

        fun put(propertyType: PropType<*>) {
            propertyTypes.put(propertyType.klass, propertyType)
        }
    }
}
