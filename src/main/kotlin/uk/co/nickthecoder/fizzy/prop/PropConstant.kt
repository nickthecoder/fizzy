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

/**
 * A property whose value can be changed (i.e. it is a var).
 */
class PropConstant<T>(value: T) : AbstractProp<T>() {

    override var value: T = value
        set(v) {
            if (v != field) {
                field = v
                listeners.fireDirty(this)
            }
        }

    override fun isConstant() = true

    override fun toString() = "Constant : $value"
}
