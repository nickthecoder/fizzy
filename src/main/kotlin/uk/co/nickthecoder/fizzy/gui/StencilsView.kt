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

import javafx.scene.Node
import javafx.scene.control.Accordion
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.model.Stencils

class StencilsView(mainWindow: MainWindow)
    : Dockable(mainWindow, "Stencils") {

    val accordion = Accordion()

    private val stencilViews = MutableFList<StencilView>()

    override fun buildContent(): Node {

        Stencils.stencils.forEach { stencil ->
            val view = StencilView(mainWindow, stencil)
            stencilViews.add(view)
            accordion.panes.add(view.build())
        }
        accordion.expandedPane = accordion.panes[0]

        return accordion
    }
}
