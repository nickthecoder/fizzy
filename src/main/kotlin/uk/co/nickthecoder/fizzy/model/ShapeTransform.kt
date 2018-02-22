package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.AngleExpression
import uk.co.nickthecoder.fizzy.prop.Dimension2Expression
import uk.co.nickthecoder.fizzy.prop.Vector2Expression

class ShapeTransform( val shape : Shape ) {

    /**
     * The position of this object relative to the parent (which is either a Group, or a Document).
     */
    val position = Dimension2Expression("Dimension2(0mm, 0mm)", shape.context)

    /**
     * The local position of this object, which corresponds to the [position] within the parent.
     * It is also used as the center of rotation.
     * (0,0) would be the top-left of the shape, and [size] would be the bottom right.
     * The default value is [size] / 2, which is the center of the shape.
     */
    val localPosition = Dimension2Expression("size / 2", shape.context)

    val size = Dimension2Expression("Dimension2(1mm,1mm)")

    // Should we have a scale? A scale would scale the line widths, the fonts etc
    val scale = Vector2Expression("Vector2(1, 1)", shape.context)

    val rotation = AngleExpression("0 deg", shape.context)

    init {
        position.listeners.add(shape)
        localPosition.listeners.add(shape)
        size.listeners.add(shape)
        scale.listeners.add(shape)
        rotation.listeners.add(shape)
    }
}
