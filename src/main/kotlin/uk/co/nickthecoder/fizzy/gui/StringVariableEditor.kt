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
package uk.co.nickthecoder.fizzy.gui

import javafx.scene.control.TextField
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener
import uk.co.nickthecoder.fizzy.prop.PropVariable

class StringVariableEditor(val variable: PropVariable<String>)
    : TextField(variable.value), PropListener {

    init {
        variable.propListeners.add(this)
        styleClass.add("expression")
        prefColumnCount = 30

        focusedProperty().addListener { _, _, hasFocus ->
            if (!hasFocus) {
                if (variable.value != text) {
                    variable.value = text
                    update()
                }
            }
        }
        update()
    }

    fun update() {
        text = variable.value

        styleClass.remove("error")
        // TODO Add test for matching regex, and set a styleClass of error
    }

    override fun dirty(prop: Prop<*>) {
        update()
    }
}
