package uk.co.nickthecoder.fizzy.model

enum class StrokeCap {
    BUTT, SQUARE, ROUND;

    fun toFormula() = "STROKE_CAP_${name}"

}
