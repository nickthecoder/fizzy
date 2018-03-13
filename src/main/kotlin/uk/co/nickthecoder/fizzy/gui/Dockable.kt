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
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

abstract class Dockable(val mainWindow: MainWindow, val title: String)
    : BuildableNode {

    val borderPane = BorderPane()

    val titlePane = HBox()

    val titleLabel = Label(title)

    override fun build(): Node {
        titlePane.styleClass.add("dock-title")

        titlePane.children.add(titleLabel)
        borderPane.top = titlePane
        borderPane.center = buildContent()
        return borderPane
    }

    abstract fun buildContent(): Node
}
