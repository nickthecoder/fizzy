/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.util.ratio
import uk.co.nickthecoder.fizzy.util.terse
import uk.co.nickthecoder.fizzy.util.toFormula

/**
 * Holds a length as a number with a given unit of measures.
 * For example, 1.5 meters could be stored as 1.5m or 1500mm or 150cm.
 *
 * Later on it could also be stored in yards/feet/inches/points too by adding extra values to the Units class.
 */
class Dimension : Comparable<Dimension> {

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
     * Creates a dimension using default units.
     */
    internal constructor(inDefaultUnits: Double) {
        this.inDefaultUnits = inDefaultUnits
        this.units = Units.mm
        this.power = 1.0
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
        km(1000000.0),
        inch(25.4), // Note, cannot use "in" because that is a Kotlin keyword
        ft(25.4 * 12),
        yard(25.4 * 36),
        pt(25.4 / 72) // A point ( 1/72 of an inch )
    }

    val mm get() = inDefaultUnits
    val cm get() = inUnits(Units.cm)
    val m get() = inUnits(Units.m)
    val km get() = inUnits(Units.km)
    val inch get() = inUnits(Units.inch)
    val ft get() = inUnits(Units.ft)
    val yard get() = inUnits(Units.yard)
    val pt get() = inUnits(Units.pt)

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

    operator fun times(b: Vector2): Dimension2 {
        return Dimension2(this * b.x, this * b.y)
    }

    operator fun div(b: Double): Dimension {
        return Dimension(units, inDefaultUnits / b, power)
    }

    operator fun div(b: Dimension): Dimension {
        return Dimension(inUnits(units) / b.inUnits(units), units, power - b.power)
    }

    operator fun div(b: Vector2): Dimension2 {
        return Dimension2(this / b.x, this / b.y)
    }


    fun ratio(b: Dimension): Double {
        assert(power == b.power)
        return inDefaultUnits.ratio(b.inDefaultUnits)
    }

    fun sqrt(): Dimension {
        return Dimension(Math.sqrt(inUnits(units)), units, power / 2)
    }

    fun isNear(other: Dimension) = Math.abs(inDefaultUnits - other.inDefaultUnits) < 0.001

    /**
     * Return in a format suitable for a formula.
     * Note. the space before the units is important for value of "NaN"
     */
    fun toFormula() = "${inUnits(units).toFormula()} ${units.name}"


    override fun hashCode(): Int = 13 * inDefaultUnits.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is Dimension) {
            return inDefaultUnits == other.inDefaultUnits
        }
        return false
    }

    override fun compareTo(other: Dimension): Int {
        return inDefaultUnits.compareTo(other.inDefaultUnits)
    }

    fun min(other: Dimension) = if (other < this) other else this

    fun max(other: Dimension) = if (other > this) other else this

    override fun toString(): String {
        return if (power == 0.0) {
            "$inDefaultUnits"
        } else {
            "${inUnits(units).terse()} ${units.name}${if (power == 1.0) "" else "^$power"}"
        }
    }

    companion object {
        val ZERO_mm = Dimension(0.0, Units.mm, 1.0)
        val ONE_POINT = Dimension(1.0, Units.pt)
    }
}
