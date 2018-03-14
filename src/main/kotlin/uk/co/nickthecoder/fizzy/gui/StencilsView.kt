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
import javafx.scene.control.TitledPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.model.Stencils

class StencilsView(mainWindow: MainWindow)
    : Dockable(mainWindow, "Stencils") {

    val vBox = VBox()

    private val stencilViews = MutableFList<StencilView>()

    private var localStencilViewNode: TitledPane? = null

    override fun buildContent(): Node {

        Stencils.stencils.forEach { stencil ->
            val view = StencilView(mainWindow, stencil)
            stencilViews.add(view)
            vBox.children.add(view.build())
        }

        mainWindow.documentProperty.addListener { _, _, document ->
            localStencilViewNode?.let { vBox.children.remove(it) }
            if (document != null) {
                localStencilViewNode = StencilView(mainWindow, document, true).build()
                vBox.children.add(localStencilViewNode)
            }
        }

        return vBox
    }
}
