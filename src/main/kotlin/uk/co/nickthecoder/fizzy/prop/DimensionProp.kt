package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.evaluator.Context
import uk.co.nickthecoder.fizzy.evaluator.constantsContext
import uk.co.nickthecoder.fizzy.model.Dimension

class DimensionPropType private constructor()
    : PropType<Dimension>(Dimension::class) {

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

    companion object {
        val instance = DimensionPropType()

        init {
            PropType.put(instance)
        }

        fun create(a: Prop<Double>, units: Dimension.Units, power: Double = 1.0): Prop<Dimension> {
            if (a.isConstant()) {
                return PropConstant(Dimension(a.value, units, power))
            } else {
                return PropCalculation1(a) { av -> Dimension(av, units, power) }
            }
        }
    }
}

class DimensionExpression(expression: String, context: Context = constantsContext)
    : PropExpression<Dimension>(expression, Dimension::class, context)

fun dimensionConversion(a: Prop<*>, units: Dimension.Units, power: Double = 1.0): Prop<*> {
    if (a.value is Double) {
        @Suppress("UNCHECKED_CAST")
        return DimensionPropType.create(a as Prop<Double>, units, power)
    }
    return throwExpectedType("Double", a)
}

fun mmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.mm)
fun cmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.cm)
fun mConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.m)
fun kmConversion(a: Prop<*>): Prop<*> = dimensionConversion(a, Dimension.Units.km)
