package uk.co.nickthecoder.fizzy.model

/**
 * Holds a length as a number with a given unit of measures.
 * For example, 1.5 meters could be stored as 1.5m or 1500mm or 150cm.
 *
 * Later on it could also be stored in yards/feet/inches/points too by adding extra values to the Units class.
 */
class Dimension(val number: Double, val units: Units, val power: Double = 1.0) {

    enum class Units(val scale: Double) {
        mm(1.0 / 1000.0),
        cm(1.0 / 100.0),
        m(1.0),
        km(1000.0)
    }

    val mm = inUnits(Units.mm)
    val cm = inUnits(Units.cm)
    val m = inUnits(Units.m)
    val km = inUnits(Units.km)

    fun inUnits(units: Units): Double = number / Math.pow(units.scale, power) * Math.pow(this.units.scale, power)

    operator fun plus(b: Dimension): Dimension {
        assert(power == b.power)
        return Dimension(number + b.inUnits(units), units, power)
    }

    operator fun minus(b: Dimension): Dimension {
        assert(power == b.power)
        return Dimension(number - b.inUnits(units), units, power)
    }

    operator fun times(b: Double): Dimension {
        return Dimension(number * b, units, power)
    }

    operator fun times(b: Dimension): Dimension {
        return Dimension(number * b.inUnits(units), units, power + b.power)
    }

    operator fun div(b: Double): Dimension {
        return Dimension(number / b, units, power)
    }

    operator fun div(b: Dimension): Dimension {
        return Dimension(number / b.inUnits(units), units, power - b.power)
    }

    override fun toString(): String {
        return if (power == 0.0) {
            "$number"
        } else {
            "$number ${units.name} ${if (power == 1.0) "" else "^$power"}"
        }
    }

    companion object {
        val ZERO_mm = Dimension(0.0, Units.mm, 1.0)
    }
}
