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

        val CLOSE_BRACKET = CloseBracketOperator(6)
        val DOT = DotOperator(7)
        val UNARY_MINUS = UnaryMinusOperator(8)

        fun add(vararg ops: Operator) {
            ops.forEach { operators.put(it.str, it) }
        }

        init {
            add(OPEN_BRACKET, COMMA, PLUS, MINUS, TIMES, DIV, CLOSE_BRACKET, DOT)
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
        if (a is PropMethod<*, *>) {
            a.applyArgs(b)
            // Make sure that the value can be evaluated, so that if it fails, then the error is reported at
            // the correct place.
            a.value
            return a
        } else {
            return cannotApply(a, b)
        }
    }
}

class DotOperator(precedence: Int) : BinaryOperator(".", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (b is FieldOrMethodName) {
            PropType.field(a, b.value)?.let { field ->
                return field
            }
        }
        throw RuntimeException("Expected a field or method name, but found ${b.value}")
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
            return PropCalculation2(a as Prop<Double>, b as Prop<Double>) { av, bv -> av + bv }

        } else if (a.value is Angle && b.value is Angle) {
            return PropCalculation2(a as Prop<Angle>, b as Prop<Angle>) { av, bv -> av + bv }

        } else if (a.value is Dimension && b.value is Dimension) {
            return PropCalculation2(a as Prop<Dimension>, b as Prop<Dimension>) { av, bv -> av + bv }

        } else if (a.value is Dimension2 && b.value is Dimension2) {
            return PropCalculation2(a as Prop<Dimension2>, b as Prop<Dimension2>) { av, bv -> av + bv }

        } else if (a.value is Vector2 && b.value is Vector2) {
            return PropCalculation2(a as Prop<Vector2>, b as Prop<Vector2>) { av, bv -> av + bv }

        } else if (a.value is String && b.value is String) {
            return PropCalculation2(a as Prop<String>, b as Prop<String>) { av, bv -> av + bv }

        }

        return cannotApply(a, b)
    }
}

@Suppress("UNCHECKED_CAST")
class UnaryMinusOperator(precedence: Int) : UnaryOperator("-", precedence) {
    override fun apply(a: Prop<*>): Prop<*> {

        if (a.value is Double) {
            return PropCalculation1<Double, Double>(a as Prop<Double>) { -it }

        } else if (a.value is Angle) {
            return PropCalculation1(a as Prop<Angle>) { -it }

        } else if (a.value is Dimension) {
            return PropCalculation1(a as Prop<Dimension>) { -it }

        } else if (a.value is Dimension2) {
            return PropCalculation1(a as Prop<Dimension2>) { -it }

        } else if (a.value is Vector2) {
            return PropCalculation1(a as Prop<Vector2>) { -it }

        }

        return cannotApply(a)
    }

}

@Suppress("UNCHECKED_CAST")
class MinusOperator(precedence: Int) : BinaryOperator("-", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {

        if (a.value is Double && b.value is Double) {
            return PropCalculation2(a as Prop<Double>, b as Prop<Double>) { av, bv -> av - bv }

        } else if (a.value is Angle && b.value is Angle) {
            return PropCalculation2(a as Prop<Angle>, b as Prop<Angle>) { av, bv -> av - bv }

        } else if (a.value is Dimension && b.value is Dimension) {
            return PropCalculation2(a as Prop<Dimension>, b as Prop<Dimension>) { av, bv -> av - bv }

        } else if (a.value is Dimension2 && b.value is Dimension2) {
            return PropCalculation2(a as Prop<Dimension2>, b as Prop<Dimension2>) { av, bv -> av - bv }

        } else if (a.value is Vector2 && b.value is Vector2) {
            return PropCalculation2(a as Prop<Vector2>, b as Prop<Vector2>) { av, bv -> av - bv }

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
                return PropCalculation2(a as Prop<Double>, b as Prop<Double>) { av, bv -> av * bv }

            } else if (b.value is Vector2) {
                return PropCalculation2(b as Prop<Vector2>, a as Prop<Double>) { av, bv -> av * bv }

            } else if (b.value is Angle) {
                return PropCalculation2(b as Prop<Angle>, a as Prop<Double>) { av, bv -> av * bv }

            } else if (b.value is Dimension) {
                return PropCalculation2(b as Prop<Dimension>, a as Prop<Double>) { av, bv -> av * bv }

            } else if (b.value is Dimension2) {
                return PropCalculation2(b as Prop<Dimension2>, a as Prop<Double>) { av, bv -> av * bv }
            }

        } else if (a.value is Angle && b.value is Double) {
            return PropCalculation2(a as Prop<Angle>, b as Prop<Double>) { av, bv -> av * bv }

        } else if (a.value is Dimension) {
            if (b.value is Dimension) {
                return PropCalculation2(a as Prop<Dimension>, b as Prop<Dimension>) { av, bv -> av * bv }

            } else if (b.value is Double) {
                return PropCalculation2(a as Prop<Dimension>, b as Prop<Double>) { av, bv -> av * bv }
            }

        } else if (a.value is Dimension2) {
            if (b.value is Dimension2) {
                return PropCalculation2(a as Prop<Dimension2>, b as Prop<Dimension2>) { av, bv -> av * bv }

            } else if (b.value is Double) {
                return PropCalculation2(a as Prop<Dimension2>, b as Prop<Double>) { av, bv -> av * bv }

            } else if (b.value is Vector2) {
                return PropCalculation2(a as Prop<Dimension2>, b as Prop<Vector2>) { av, bv -> av * bv }
            }

        } else if (a.value is Vector2) {
            if (b.value is Vector2) {
                return PropCalculation2(a as Prop<Vector2>, b as Prop<Vector2>) { av, bv -> av * bv }

            } else if (b.value is Double) {
                return PropCalculation2(a as Prop<Vector2>, b as Prop<Double>) { av, bv -> av * bv }

            } else if (b.value is Dimension2) {
                return PropCalculation2(b as Prop<Dimension2>, a as Prop<Vector2>) { av, bv -> av * bv }
            }

        }

        return cannotApply(a, b)
    }
}

@Suppress("UNCHECKED_CAST")
class DivOperator(precedence: Int) : BinaryOperator("/", precedence) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return PropCalculation2(a as Prop<Double>, b as Prop<Double>) { av, bv -> av / bv }

        } else if (a.value is Angle) {
            if (b.value is Double) {
                return PropCalculation2(a as Prop<Angle>, b as Prop<Double>) { av, bv -> av / bv }

            } else if (b.value is Angle) {
                return PropCalculation2(a as Prop<Angle>, b as Prop<Angle>) { av, bv -> av / bv }
            }

        } else if (a.value is Dimension) {
            if (b.value is Dimension) {
                return PropCalculation2(a as Prop<Dimension>, b as Prop<Dimension>) { av, bv -> av / bv }

            } else if (b.value is Double) {
                return PropCalculation2(a as Prop<Dimension>, b as Prop<Double>) { av, bv -> av / bv }
            }

        } else if (a.value is Dimension2) {
            if (b.value is Dimension2) {
                return PropCalculation2(a as Prop<Dimension2>, b as Prop<Dimension2>) { av, bv -> av / bv }

            } else if (b.value is Double) {
                return PropCalculation2(a as Prop<Dimension2>, b as Prop<Double>) { av, bv -> av / bv }

            } else if (b.value is Vector2) {
                return PropCalculation2(a as Prop<Dimension2>, b as Prop<Vector2>) { av, bv -> av / bv }
            }

        } else if (a.value is Vector2) {
            if (b.value is Vector2) {
                return PropCalculation2(a as Prop<Vector2>, b as Prop<Vector2>) { av, bv -> av / bv }

            } else if (b.value is Double) {
                return PropCalculation2(a as Prop<Vector2>, b as Prop<Double>) { av, bv -> av / bv }
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
