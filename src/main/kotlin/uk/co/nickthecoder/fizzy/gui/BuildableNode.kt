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

/**
 * Note. This is NOT a subclass of [Node], instead it call [build] ONCE to obtain the [Node] to add to the scene.
 * This allows the implementation to be hidden (and also makes development easier, because intellisense will not be
 * polluted by Node's methods).
 */

interface BuildableNode {
    fun build(): Node
}