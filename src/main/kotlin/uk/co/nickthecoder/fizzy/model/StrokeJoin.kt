package uk.co.nickthecoder.fizzy.model

enum class StrokeJoin {

    BEVEL, MITER, ROUND;

    fun toFormula() = "STROKE_JOIN_${name}"

}
