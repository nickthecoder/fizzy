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
package uk.co.nickthecoder.fizzy.util

import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.fizzy.model.Document
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * This interface seems fairly useless at the moment, but at a later date, it will include
 * operations to save embedded objects, such as png images, or svg files.
 * There will be a "plain" implementation where embedded objects are not allowed, and the
 * output is a plain json file.
 * Another implementation will allow embedded objects, and will save as a zip file.
 */
interface FizzyWriter {
    fun save()
}

class FizzyJsonWriter(val document: Document, val file: File)
    : FizzyWriter {

    override fun save() {
        val json: JsonObject = DocumentToJson(document).toJson()

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            json.writeTo(it, PrettyPrint.indentWithSpaces(2))
        }
    }

}
