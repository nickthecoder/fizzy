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

class StringPropType private constructor()
    : PropType<String>(String::class) {

    override fun findField(prop: Prop<String>, name: String): PropField<String, *>? {
        return when (name) {
            "length" -> PropField<String, Double>(prop) { prop.value.length.toDouble() }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<String>, name: String): PropMethod<String, *>? {
        return when (name) {
            "head" -> PropMethod1(prop, Double::class) { a -> prop.value.substring(0, a.toInt()) }
            "tail" -> PropMethod1(prop, Double::class) { a ->
                val l = prop.value.length
                prop.value.substring(l - a.toInt(), l)
            }
            "substring" -> PropMethod2(prop, Double::class, Double::class) { a, b -> prop.value.substring(a.toInt(), b.toInt()) }
            else -> null
        }
    }

    companion object {
        val instance = StringPropType()
    }
}
