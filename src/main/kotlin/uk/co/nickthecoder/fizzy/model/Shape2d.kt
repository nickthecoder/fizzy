package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.evaluator.CompoundContext
import uk.co.nickthecoder.fizzy.evaluator.ThisContext
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.prop.*

class Shape2d(parent: Parent)
    : Shape(parent) {

    override val context = CompoundContext(listOf(
            constantsContext, ThisContext(PropConstant(this), Shape2dPropType.instance)))

    /**
     * The position of this object relative to the parent (which is either a Group, or a Document).
     */
    val position = Dimension2Expression("Dimension2(0mm, 0mm)", context)

    /**
     * The local position of this object, which corresponds to the [position] within the parent.
     * It is also used as the center of rotation.
     * (0,0) would be the top-left of the shape, and [size] would be the bottom right.
     * The default value is [size] / 2, which is the center of the shape.
     */
    val localPosition = Dimension2Expression("size / 2", context)

    val size = Dimension2Expression("Dimension2(1mm,1mm)")

    // Should we have a scale? A scale would scale the line widths, the fonts etc
    val scale = Vector2Expression("Vector2(1, 1)", context)

    val rotation = AngleExpression("0 deg", context)

    init {
        position.listeners.add(this)
        localPosition.listeners.add(this)
        size.listeners.add(this)
        scale.listeners.add(this)
        rotation.listeners.add(this)
    }

}
