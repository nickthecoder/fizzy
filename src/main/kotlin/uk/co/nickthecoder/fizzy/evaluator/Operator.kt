package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.model.Angle
import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import uk.co.nickthecoder.fizzy.prop.*

abstract class Operator(val str: String, val precedence: Int) {
    abstract fun apply(values: MutableList<Prop<*>>): Prop<*>

    /**
     * Is there is a unary version of this operator?
     * For example MinusOperator is a BinaryOperator, but also has a unary version (UnaryMinusOperator).
     */
    open val unaryOperator: Operator? = null

    fun expectsValue(): Boolean = true

    override fun toString() = "${this.javaClass.simpleName} $str"

    companion object {

        val operators = mutableMapOf<String, Operator>()

        val NONE = NoOperator(0)

        val OPEN_BRACKET = OpenBracketOperator(1)

        val APPLY = ApplyOperator(2)

        val COMMA = CommaOperator(3)

        val PLUS = PlusOperator(4)
        val MINUS = MinusOperator(4)

        val TIMES = TimesOperator(5)
        val DIV = DivOperator(5)

        val UNARY_MINUS = UnaryMinusOperator(6)
        val CLOSE_BRACKET = CloseBracketOperator(7)

        fun add(vararg ops: Operator) {
            ops.forEach { operators.put(it.str, it) }
        }

        init {
            add(OPEN_BRACKET, COMMA, PLUS, MINUS, TIMES, DIV, CLOSE_BRACKET)
        }

        fun find(str: String): Operator? = operators[str]

        fun isValid(str: String): Boolean = operators.containsKey(str)
    }
}

class NoOperator(precedence: Int) : Operator("", precedence) {
    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        throw RuntimeException("Unexpected use of the NoOperator")
    }
}

interface OpenBracket

class OpenBracketOperator(precedence: Int) : UnaryOperator("(", precedence), OpenBracket {
    override fun apply(a: Prop<*>): Prop<*> {
        throw RuntimeException("Cannot apply '('")
    }
}

class CloseBracketOperator(precedence: Int) : Operator(")", precedence) {
    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        throw RuntimeException("Cannot apply ')'")
    }
}

class ApplyOperator(precedence: Int) : BinaryOperator("(", precedence), OpenBracket {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a is Function) {
            return a.call(b)
        } else {
            return cannotApply(a, b)
        }
    }
}

/**
 * Unary operators, which precede their operands, such as unary minus (e.g. -1)
 */
abstract class UnaryOperator(str: String, precedence: Int) : Operator(str, precedence) {

    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        if (values.size < 1) {
            throw RuntimeException("Operator $str must have an operand")
        }
        val a = values.removeAt(values.size - 1)
        return apply(a)
    }

    protected fun cannotApply(a: Prop<*>): Prop<*> {
        throw RuntimeException("Cannot apply $str to ${a.value}")
    }

    abstract fun apply(a: Prop<*>): Prop<*>
}

/**
 * Binary operators have two operands, one is before the operator, the other after it. e.g. 1 + 2
 */
abstract class BinaryOperator(str: String, precedence: Int) : Operator(str, precedence) {

    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        if (values.size < 2) {
            throw RuntimeException("Operator $str must have two operands")
        }
        val b = values.removeAt(values.size - 1)
        val a = values.removeAt(values.size - 1)
        return apply(a, b)
    }

    protected fun cannotApply(a: Prop<*>, b: Prop<*>): Prop<*> {
        throw RuntimeException("Cannot apply $str to ${a.value} and ${b.value}")
    }

    abstract fun apply(a: Prop<*>, b: Prop<*>): Prop<*>

}

@Suppress("UNCHECKED_CAST")
class PlusOperator(precedence: Int) : BinaryOperator("+", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {

        if (a.value is Double && b.value is Double) {
            return DoublePlus(a as DoubleProp, b as DoubleProp)

        } else if (a.value is Angle && b.value is Angle) {
            return AnglePlus(a as AngleProp, b as AngleProp)

        } else if (a.value is Dimension && b.value is Dimension) {
            return DimensionPlus(a as DimensionProp, b as DimensionProp)

        } else if (a.value is Dimension2 && b.value is Dimension2) {
            return Dimension2Plus(a as Dimension2Prop, b as Dimension2Prop)

        } else if (a.value is Vector2 && b.value is Vector2) {
            return Vector2Plus(a as Vector2Prop, b as Vector2Prop)

        } else if (a.value is String && b.value is String) {
            return StringPlus(a as Prop<String>, b as Prop<String>)

        }

        return cannotApply(a, b)
    }
}

@Suppress("UNCHECKED_CAST")
class UnaryMinusOperator(precedence: Int) : UnaryOperator("-", precedence) {
    override fun apply(a: Prop<*>): Prop<*> {

        if (a.value is Double) {
            return DoubleMinus(DoubleConstant(0.0), a as DoubleProp)

        } else if (a.value is Angle) {
            return AngleUnaryMinus(a as AngleProp)

        } else if (a.value is Dimension) {
            return DimensionUnaryMinus(a as DimensionProp)

        } else if (a.value is Dimension2) {
            return Dimension2UnaryMinus(a as Dimension2Prop)

        } else if (a.value is Vector2) {
            return Vector2Minus(Vector2Constant(), a as Vector2Prop)

        }

        return cannotApply(a)
    }

}

@Suppress("UNCHECKED_CAST")
class MinusOperator(precedence: Int) : BinaryOperator("-", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {

        if (a.value is Double && b.value is Double) {
            return DoubleMinus(a as DoubleProp, b as DoubleProp)

        } else if (a.value is Angle && b.value is Angle) {
            return AngleMinus(a as AngleProp, b as AngleProp)

        } else if (a.value is Dimension && b.value is Dimension) {
            return DimensionMinus(a as DimensionProp, b as DimensionProp)

        } else if (a.value is Dimension2 && b.value is Dimension2) {
            return Dimension2Minus(a as Dimension2Prop, b as Dimension2Prop)

        } else if (a.value is Vector2 && b.value is Vector2) {
            return Vector2Minus(a as Vector2Prop, b as Vector2Prop)

        }
        return cannotApply(a, b)
    }

    override val unaryOperator: Operator? get() = Operator.UNARY_MINUS
}


@Suppress("UNCHECKED_CAST")
class TimesOperator(precedence: Int) : BinaryOperator("*", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double) {
            if (b.value is Double) {
                return DoubleTimes(a as DoubleProp, b as DoubleProp)

            } else if (b.value is Vector2) {
                return Vector2TimesDouble(b as Vector2Prop, a as DoubleProp)

            } else if (b.value is Angle) {
                return AngleTimesDouble(b as AngleProp, a as DoubleProp)

            } else if (b.value is Dimension) {
                return DimensionTimesDouble(b as DimensionProp, a as DoubleProp)

            } else if (b.value is Dimension2) {
                return Dimension2TimesDouble(b as Dimension2Prop, a as DoubleProp)
            }

        } else if (a.value is Angle && b.value is Double) {
            return AngleTimesDouble(a as AngleProp, b as DoubleProp)

        } else if (a.value is Dimension) {
            if (b.value is Dimension) {
                return DimensionTimes(a as DimensionProp, b as DimensionProp)

            } else if (b.value is Double) {
                return DimensionTimesDouble(a as DimensionProp, b as DoubleProp)
            }

        } else if (a.value is Dimension2) {
            if (b.value is Dimension2) {
                return Dimension2Times(a as Dimension2Prop, b as Dimension2Prop)

            } else if (b.value is Double) {
                return Dimension2TimesDouble(a as Dimension2Prop, b as DoubleProp)

            } else if (b.value is Vector2) {
                return Dimension2TimesVector2(a as Dimension2Prop, b as Vector2Prop)
            }

        } else if (a.value is Vector2) {
            if (b.value is Vector2) {
                return Vector2Times(a as Vector2Prop, b as Vector2Prop)

            } else if (b.value is Double) {
                return Vector2TimesDouble(a as Vector2Prop, b as DoubleProp)

            } else if (b.value is Dimension2) {
                return Dimension2TimesVector2(b as Dimension2Prop, a as Vector2Prop)
            }

        }

        return cannotApply(a, b)
    }
}

@Suppress("UNCHECKED_CAST")
class DivOperator(precedence: Int) : BinaryOperator("/", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return DoubleDiv(a as DoubleProp, b as DoubleProp)

        } else if (a.value is Angle) {
            if (b.value is Double) {
                return AngleDivDouble(a as AngleProp, b as DoubleProp)

            } else if (b.value is Angle) {
                return AngleDiv(a as AngleProp, b as AngleProp)
            }

        } else if (a.value is Dimension) {
            if (b.value is Dimension) {
                return DimensionDiv(a as DimensionProp, b as DimensionProp)

            } else if (b.value is Double) {
                return DimensionDivDouble(a as DimensionProp, b as DoubleProp)
            }

        } else if (a.value is Dimension2) {
            if (b.value is Dimension2) {
                return Dimension2Div(a as Dimension2Prop, b as Dimension2Prop)

            } else if (b.value is Double) {
                return Dimension2DivDouble(a as Dimension2Prop, b as DoubleProp)

            } else if (b.value is Vector2) {
                return Dimension2DivVector2(a as Dimension2Prop, b as Vector2Prop)
            }

        } else if (a.value is Vector2) {
            if (b.value is Vector2) {
                return Vector2Div(a as Vector2Prop, b as Vector2Prop)

            } else if (b.value is Double) {
                return Vector2DivDouble(a as Vector2Prop, b as DoubleProp)
            }

        }

        return cannotApply(a, b)
    }
}

class CommaOperator(precedence: Int) : BinaryOperator(",", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a is ArgList) {
            a.value.add(b)
            return a
        } else {
            val list = ArgList()
            list.value.add(a)
            list.value.add(b)
            return list
        }
    }
}
