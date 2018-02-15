package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.DoubleValue
import uk.co.nickthecoder.fizzy.prop.LinkedProp
import uk.co.nickthecoder.fizzy.prop.StringValue
import uk.co.nickthecoder.fizzy.prop.Vector2Value

open class Shape {

    var parent : Parent? = null

    val id = LinkedProp<String>(StringValue(generateId()))

    val position = LinkedProp<Vector2>(Vector2Value(Vector2(0.0, 0.0)))

    val scale = LinkedProp<Vector2>(Vector2Value(Vector2(1.0, 1.0)))

    val rotation = LinkedProp<Double>(DoubleValue(0.0))

    companion object {
        private var previousId = 0

        fun generateId(): String {
            previousId++
            return "shape$previousId"
        }
    }


}
