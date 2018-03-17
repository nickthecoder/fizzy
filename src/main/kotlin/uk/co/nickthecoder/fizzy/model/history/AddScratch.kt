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
package uk.co.nickthecoder.fizzy.model.history

import uk.co.nickthecoder.fizzy.model.Scratch
import uk.co.nickthecoder.fizzy.model.Shape

class AddScratch(val shape: Shape, val index: Int, val scratch: Scratch)
    : Change {

    override fun redo() {
        shape.scratches.add(index, scratch)
    }

    override fun undo() {
        shape.scratches.removeAt(index)
    }
}
