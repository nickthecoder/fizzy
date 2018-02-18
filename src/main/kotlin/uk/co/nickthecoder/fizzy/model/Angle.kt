package uk.co.nickthecoder.fizzy.model

class Angle private constructor(val radians: Double) {

    val degrees: Double
        get() = radians / Math.PI * 180.0

    companion object {

        fun degrees(d: Double) = Angle(d / 180.0 * Math.PI)

        fun radians(r: Double) = Angle(r)

        val ZERO = Angle(0.0)
        val PI = Angle(Math.PI)
        val TAU = Angle(Math.PI * 2.0)
    }

    override fun equals(other: Any?): Boolean {
        return other is Angle && other.radians == this.radians
    }
}
