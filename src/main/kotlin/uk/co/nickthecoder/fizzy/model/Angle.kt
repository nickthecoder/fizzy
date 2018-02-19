package uk.co.nickthecoder.fizzy.model

class Angle private constructor(val radians: Double) {

    val degrees: Double
        get() = radians / Math.PI * 180.0

    operator fun plus(other: Angle) = Angle.radians(radians + other.radians)

    operator fun minus(other: Angle) = Angle.radians(radians - other.radians)

    operator fun unaryMinus() = Angle.radians(-radians)

    operator fun times(other: Double) = Angle.radians(radians * other)

    operator fun div(other: Double) = Angle.radians(radians / other)

    operator fun div(other: Angle) = radians / other.radians

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
