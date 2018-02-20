package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension

class DimensionPropType : PropType<Dimension>(Dimension::class) {

    override fun findField(prop: Prop<Dimension>, name: String): PropField<Dimension, *>? {
        return when (name) {
            "mm" -> PropField<Dimension, Double>(prop) { prop.value.mm }
            "cm" -> PropField<Dimension, Double>(prop) { prop.value.cm }
            "m" -> PropField<Dimension, Double>(prop) { prop.value.m }
            "km" -> PropField<Dimension, Double>(prop) { prop.value.km }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<Dimension>, name: String): PropMethod<Dimension, *>? {
        return when (name) {
            "ratio" -> PropMethod1(prop, Dimension::class) { prop.value.ratio(it) }
            else -> null
        }
    }
}

class DimensionExpression(expression: String, context: Context = constantsContext)
    : PropExpression<Dimension>(expression, Dimension::class, context)

class DimensionConstant(value: Dimension = Dimension.ZERO_mm)
    : PropConstant<Dimension>(value) {

    companion object {
        fun create(a: Prop<Double>, units: Dimension.Units, power: Double = 1.0): Prop<Dimension> {
            if (a is DoubleConstant) {
                return DimensionConstant(Dimension(a.value, units, power))
            } else {
                return DimensionPropLinked(a, units, power)
            }
        }
    }
}

class DimensionPropLinked(val number: Prop<Double>, val units: Dimension.Units, val power: Double = 1.0)
    : PropCalculation<Dimension>() {

    init {
        number.listeners.add(this)
    }

    override fun eval() = Dimension(number.value, units, power)
}

fun dimensionConversion(a: Prop<*>, units: Dimension.Units, power: Double = 1.0): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return DimensionConstant.create(a as Prop<Double>, units, power)
    }
    return throwExpectedType("Double", a)
}

fun mmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.mm)
fun cmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.cm)
fun mConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.m)
fun kmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.km)
