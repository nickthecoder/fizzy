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

interface PropListener {

    /**
     * The property being listened to has changed. In the case of [PropCalculation], the value may not have
     * been re-evaluated yet, and therefore the next time [Prop.value] is referenced, it will cause a re-evaluation.
     * In the case of [PropConstant], the new value will already be known.
     */
    fun dirty(prop: Prop<*>)

}
