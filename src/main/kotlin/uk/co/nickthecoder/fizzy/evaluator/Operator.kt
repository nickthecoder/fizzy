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

    override fun toString() = "${this.javaClass.simpleName} $str"

    companion object {

        val operators = mutableMapOf<String, Operator>()

        val NONE = NoOperator()

        val PLUS = PlusOperator()
        val MINUS = MinusOperator()
        val UNARY_MINUS = UnaryMinusOperator()
        val DIV = DivOperator()
        val TIMES = TimesOperator()
        val OPEN_BRACKET = OpenBracketOperator()
        val CLOSE_BRACKET = CloseBracketOperator()

        val APPLY = ApplyOperator()

        fun add(vararg ops: Operator) {
            ops.forEach { operators.put(it.str, it) }
        }

        init {
            add(PLUS, MINUS, DIV, TIMES, OPEN_BRACKET, CLOSE_BRACKET)
        }

        fun find(str: String): Operator? = operators.get(str)

        fun isValid(str: String): Boolean = operators.containsKey(str)
    }
}

class NoOperator() : Operator("", 0) {
    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        throw RuntimeException("Unexpedted use of the NoOperator")
    }
}

interface OpenBracket

class OpenBracketOperator : UnaryOperator("(", 0), OpenBracket {
    override fun apply(a: Prop<*>): Prop<*> {
        throw RuntimeException("Cannot apply '('")
    }
}

class CloseBracketOperator : Operator(")", Int.MAX_VALUE) {
    override fun apply(values: MutableList<Prop<*>>): Prop<*> {
        throw RuntimeException("Cannot apply ')'")
    }
}

class ApplyOperator : BinaryOperator("(", 0), OpenBracket {
    override fun apply(a: Prop<*>, b: Prop<*>): Prop<*> {
        if (a is Function) {
            val arguments = b.value
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

    override val unaryOperator: Operator? get() = Operator.UNARY_MINUS
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

