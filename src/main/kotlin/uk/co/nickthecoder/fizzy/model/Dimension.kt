package uk.co.nickthecoder.fizzy.model

/**
 * Holds a length as a number with a given unit of measures.
 * For example, 1.5 meters could be stored as 1.5m or 1500mm or 150cm.
 *
 * Later on it could also be stored in yards/feet/inches/points too by adding extra values to the Units class.
 */
class Dimension {

    /**
     * The size stored in default units. Currently the default units are mm, but this may change, and therefore
     * applications should tend NOT to use this property directly.
     * The one time it is safe to use this property is when the "power" is zero, because then all units are
     * equivalent (i.e. 1mm^0 == 1cm^0 == 1m^0 etc).
     * Therefore if you (10km / 5km).inDefaultUnits is safe (and will return 2)
     */
    val inDefaultUnits: Double

    val power: Double

    val units: Units

    constructor(number: Double, units: Units, power: Double = 1.0) {
        inDefaultUnits = number * Math.pow(units.scale, power)
        this.units = units
        this.power = power
    }

    constructor(number: Double, unitsString: String, power: Double = 1.0)
            : this(number, Units.valueOf(unitsString), power)

    /**
     * This constructor is useful (more efficient) when performing maths on Dimensions, but would be confusing to
     * expose, and is therefore private.
     */
    private constructor(units: Units, inDefaultUnits: Double, power: Double = 1.0) {
        this.inDefaultUnits = inDefaultUnits
        this.units = units
        this.power = power
    }

    /**
     * Units of measure in length.
     *
     * The scale is relative to the default units.
     * The default units are currently mm. However, this may change, so do NOT assume that mm are the default,
     * and therefore do NOT use scale without comparing it to another scale.
     */
    enum class Units(internal val scale: Double) {
        mm(1.0),
        cm(10.0),
        m(1000.0),
        km(1000000.0)
    }

    val mm get() = inDefaultUnits
    val cm get() = inUnits(Units.cm)
    val m get() = inUnits(Units.m)
    val km get() = inUnits(Units.km)

    fun inUnits(units: Units): Double = inDefaultUnits / Math.pow(units.scale, power)

    operator fun unaryMinus(): Dimension = Dimension(-inUnits(units), units, power)

    operator fun plus(b: Dimension): Dimension {
        assert(power == b.power)
        return Dimension(units, inDefaultUnits + b.inDefaultUnits, power)
    }

    operator fun minus(b: Dimension): Dimension {
        assert(power == b.power)
        return Dimension(units, inDefaultUnits - b.inDefaultUnits, power)
    }

    operator fun times(b: Double): Dimension {
        return Dimension(units, inDefaultUnits * b, power)
    }

    operator fun times(b: Dimension): Dimension {
        return Dimension(inUnits(units) * b.inUnits(units), units, power + b.power)
    }

    operator fun div(b: Double): Dimension {
        return Dimension(units, inDefaultUnits / b, power)
    }

    operator fun div(b: Dimension): Dimension {
        return Dimension(inUnits(units) / b.inUnits(units), units, power - b.power)
    }

    fun ratio(b: Dimension): Double {
        assert(power == b.power)
        return inDefaultUnits / b.inDefaultUnits
    }

    fun sqrt(): Dimension {
        return Dimension(Math.sqrt(inUnits(units)), units, power / 2)
    }

    override fun toString(): String {
        return if (power == 0.0) {
            "$inDefaultUnits"
        } else {
            "${inUnits(units)} ${units.name}${if (power == 1.0) "" else "^$power"}"
        }
    }

    companion object {
        val ZERO_mm = Dimension(0.0, Units.mm, 1.0)
    }
}
