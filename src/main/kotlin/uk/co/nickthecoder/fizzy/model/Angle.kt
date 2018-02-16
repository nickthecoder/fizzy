package uk.co.nickthecoder.fizzy.model

class Angle(val radians: Double) {
    val degrees: Double
        get() = radians * Math.PI / 180.0

    companion object {
        val ZERO = Angle(0.0)
        val PI = Angle(Math.PI)
        val TAU = Angle(Math.PI * 2.0)
    }
}
