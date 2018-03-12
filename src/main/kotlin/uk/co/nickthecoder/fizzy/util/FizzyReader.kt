package uk.co.nickthecoder.fizzy.util

import com.eclipsesource.json.Json
import uk.co.nickthecoder.fizzy.model.Document
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

interface FizzyReader {
    fun load(): Document
}

class FizzyJsonReader(val file: File) :
        FizzyReader {

    override fun load(): Document {
        val jRoot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()
        return JsonToDocument(jRoot).toDocument()
    }
}
