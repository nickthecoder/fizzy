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
import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropListener

class DocumentTab(
        mainWindow: MainWindow,
        val document: Document,
        title: String,
        page: Page = document.pages[0],
        singleShape: Shape? = null)

    : Tab(title) {

    var page: Page = page
        set(v) {
            field = v
        }

    val drawingArea = DrawingArea(mainWindow, page, singleShape)

    val nameListener = object : PropListener {
        override fun dirty(prop: Prop<*>) {
            this@DocumentTab.text = document.name.value
        }
    }

    init {
        content = drawingArea.build()
        if (singleShape != null) {
            document.name.propListeners.add(nameListener)
        }
    }

}
