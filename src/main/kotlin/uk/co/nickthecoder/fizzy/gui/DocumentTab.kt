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

import uk.co.nickthecoder.fizzy.model.Document
import uk.co.nickthecoder.fizzy.model.Page
import uk.co.nickthecoder.paratask.gui.MyTab

class DocumentTab(doc: Document)
    : MyTab(doc.name) {

    init {
        if (doc.pages.size == 0) {
            doc.pages.add(Page(doc))
        }
    }

    val drawingArea = DrawingArea(doc.pages[0])

    val document: Document
        get() = drawingArea.page.document

    val page: Page
        get() = drawingArea.page

    init {
        content = drawingArea.build()
    }

}
