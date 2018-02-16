package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.DoubleProp
import uk.co.nickthecoder.fizzy.prop.LinkedProp
import uk.co.nickthecoder.fizzy.prop.StringValue
import uk.co.nickthecoder.fizzy.prop.Vector2Prop

open class Shape {

    var parent : Parent? = null

    val id = LinkedProp(StringValue(generateId()))

    val position = LinkedProp(Vector2Prop(Vector2(0.0, 0.0)))

    val scale = LinkedProp(Vector2Prop(Vector2(1.0, 1.0)))

    val rotation = LinkedProp(DoubleProp(0.0))

    companion object {
        private var previousId = 0

        fun generateId(): String {
            previousId++
            return "shape$previousId"
        }
    }


}
