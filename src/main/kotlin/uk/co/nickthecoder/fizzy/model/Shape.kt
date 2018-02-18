package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.AngleExpression
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.StringConstant
import uk.co.nickthecoder.fizzy.prop.Vector2Expression

open class Shape {

    var parent: Parent? = null

    val id = StringConstant(generateId())

    val position = Dimension2Expression("Dimension2(0mm, 0mm)")

    val scale = Vector2Expression("Vector2(1, 1)")

    val rotation = AngleExpression("0 deg")

    val size = Dimension2Expression("Dimension2(1mm,1mm)")

    companion object {
        private var previousId = 0

        fun generateId(): String {
            previousId++
            return "shape$previousId"
        }
    }


}
