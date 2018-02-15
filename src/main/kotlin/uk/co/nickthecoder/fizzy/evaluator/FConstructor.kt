package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.Prop

fun classFromString(str: String): Class<*> {
    return when (str) {
        "Vector2" -> Vector2::class.java
        else -> throw RuntimeException("Unknown class $str")
    }
}

class FConstructor(str: String) : Prop<Class<*>>(classFromString(str)) {

}
