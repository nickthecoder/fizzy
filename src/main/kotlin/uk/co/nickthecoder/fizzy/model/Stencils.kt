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
package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.ListListener
import uk.co.nickthecoder.fizzy.collection.FList
import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.util.FizzyJsonReader
import java.io.File

object Stencils {

    val directories = MutableFList<File>()

    val stencils = MutableFList<Document>()

    private val directoriesListener = object : ListListener<File> {
        override fun added(list: FList<File>, item: File, index: Int) {
            addStencils(item)
        }

        override fun removed(list: FList<File>, item: File, index: Int) {
            removeStencils(item)
        }
    }

    init {
        directories.listeners.add(directoriesListener)
        directories.add(File(System.getProperty("user.home"), "fizzy stencils"))
    }

    private fun addStencils(directory: File) {
        val files = directory.listFiles()
        files.forEach { file ->
            if (file.isFile && file.extension == "fstencil") {
                try {
                    val doc = FizzyJsonReader(file).load()
                    stencils.add(doc)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun removeStencils(directory: File) {
        stencils.removeAll(stencils.filter { it.file?.startsWith(directory) == true })
    }

}
