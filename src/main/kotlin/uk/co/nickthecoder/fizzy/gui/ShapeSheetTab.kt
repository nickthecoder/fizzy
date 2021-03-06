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

import javafx.scene.control.Tab
import uk.co.nickthecoder.fizzy.model.Shape

class ShapeSheetTab(val mainWindow: MainWindow, val shape: Shape)
    : Tab("Shape ${shape.id}") {


    /**
     * A reference of the ShapeSheetView is kept, to ensure that its listeners are not gc'd.
     */
    private val ssv = ShapeSheetView(mainWindow, shape)

    init {
        content = ssv.build()
    }
}
