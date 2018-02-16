package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.ExpressionProp
import uk.co.nickthecoder.fizzy.prop.StringValue

open class Shape {

    var parent: Parent? = null

    val id = StringValue(generateId())

    val position = ExpressionProp("Vector2(0, 0)", Vector2::class, Vector2(0.0, 0.0))

    val scale = ExpressionProp("Vector2(1, 1)", Vector2::class, Vector2(1.0, 1.0))

    val rotation = ExpressionProp("0 deg", Angle::class, Angle.ZERO)

    val size = ExpressionProp("Vector2(1,1)", Vector2::class, Vector2.UNIT)

    companion object {
        private var previousId = 0

        fun generateId(): String {
            previousId++
            return "shape$previousId"
        }
    }


}
