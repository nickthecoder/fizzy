package uk.co.nickthecoder.fizzy.controller

import uk.co.nickthecoder.fizzy.model.Dimension2

data class CMouseEvent(

        val point: Dimension2,
        val button: Int,
        val isAdjust: Boolean,
        val isConstrain: Boolean,
        val scale: Double = 1.0
)
