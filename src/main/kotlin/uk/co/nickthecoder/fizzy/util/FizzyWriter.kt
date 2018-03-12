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
