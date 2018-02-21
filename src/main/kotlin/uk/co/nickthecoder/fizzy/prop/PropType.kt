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
