package uk.co.nickthecoder.fizzy.evaluator

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

    override fun toString() = "Op $str"
}

class OpenBracketOperator() : UnaryOperator("(", 0) {
    override fun apply(a: Prop<*>): Prop<*> {
        throw RuntimeException("Cannot apply '('")
    }
}

class CloseBracketOperator() : Operator(")", Int.MAX_VALUE) {
    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        throw RuntimeException("Cannot apply ')'")
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
        throw RuntimeException("Cannot apply $str to $a")
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
        throw RuntimeException("Cannot apply $str to $a and $b")
    }

    abstract fun apply(a: Prop<*>, b: Prop<*>): Prop<*>

}

class PlusOperator : BinaryOperator("+", 0) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return DoublePlus(a as Prop<Double>, b as Prop<Double>)
        } else if (a.value is Vector2 && b.value is Vector2) {
            return Vector2Plus(a as Prop<Vector2>, b as Prop<Vector2>)
        } else if (a.value is String && b.value is String) {
            return StringPlus(a as Prop<String>, b as Prop<String>)
        } else {
            return cannotApply(a, b)
        }
    }
}

class UnaryMinusOperator : UnaryOperator("-", 9) {
    override fun apply(a: Prop<*>): Prop<*> {
        if (a.value is Double) {
            return DoubleMinus(DoubleValue(0.0), a as Prop<Double>)
        } else if (a.value is Vector2) {
            return Vector2Minus(Vector2Value(), a as Prop<Vector2>)
        } else {
            return cannotApply(a)
        }
    }

}

class MinusOperator : BinaryOperator("-", 0) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return DoubleMinus(a as Prop<Double>, b as Prop<Double>)
        } else if (a.value is Vector2 && b.value is Vector2) {
            return Vector2Minus(a as Prop<Vector2>, b as Prop<Vector2>)
        } else {
            return cannotApply(a, b)
        }
    }

    override val unaryOperator: Operator? = UnaryMinusOperator()
}

class TimesOperator : BinaryOperator("*", 1) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return DoubleTimes(a as Prop<Double>, b as Prop<Double>)
        } else if (a.value is Vector2 && b.value is Vector2) {
            return Vector2Times(a as Prop<Vector2>, b as Prop<Vector2>)
        } else if (a.value is Vector2 && b.value is Double) {
            return Vector2Scale(a as Prop<Vector2>, b as Prop<Double>)
        } else {
            return cannotApply(a, b)
        }
    }
}

class DivOperator : BinaryOperator("/", 1) {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a.value is Double && b.value is Double) {
            return DoubleDiv(a as Prop<Double>, b as Prop<Double>)
        } else if (a.value is Vector2 && b.value is Vector2) {
            return Vector2Div(a as Prop<Vector2>, b as Prop<Vector2>)
        } else if (a.value is Vector2 && b.value is Double) {
            return Vector2Shrink(a as Prop<Vector2>, b as Prop<Double>)

        } else {
            return cannotApply(a, b)
        }
    }
}

enum class Operators(val op: Operator) {

    PLUS(PlusOperator()),
    MINUS(MinusOperator()),
    DIV(DivOperator()),
    TIMES(TimesOperator()),
    OPEN_BRACKET(OpenBracketOperator()),
    CLOSE_BRACKET(CloseBracketOperator());

    companion object {

        fun find(str: String): Operators? {
            values().forEach {
                if (it.op.str == str) {
                    return it
                }
            }
            return null
        }

        fun isValid(str: String): Boolean {
            values().forEach {
                if (it.op.str == str) {
                    return true
                }
            }
            return false
        }
    }
}