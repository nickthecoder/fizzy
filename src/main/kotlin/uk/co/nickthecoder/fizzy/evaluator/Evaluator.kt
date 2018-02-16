package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.DoubleValue
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.StringValue

class Evaluator(val text: CharSequence) {

    private var index = 0

    private var operators = mutableListOf<Operator>()
    private var values = mutableListOf<Prop<*>>()
    private var expectValue = true

    private var debug = false

    fun debug(): Evaluator {
        debug = true
        return this
    }

    private fun skipWhiteSpace() {
        while (text.length > index && text[index].isWhitespace()) {
            index++
        }
    }

    private fun log(str: String) {
        if (debug) {
            println(str)
        }
    }

    private fun readToken(): Token {
        //log("Reading token @ $index")
        skipWhiteSpace()
        //log("Skipped white space to $index")
        if (index >= text.length) {
            //log("End of text - Empty token")
            return Token(index)
        } else {
            val token: Token = Token(index)
            text.subSequence(index, text.length).forEach { c ->
                //log("Looking at char $c")
                if (!token.accept(c)) {
                    //log("Character $c ended token $token")
                    return token
                }
                index++
            }

            //log("End of text ended token $token")
            return token
        }
    }

    fun parse(): Prop<*> {
        log("Parsing text $text")
        while (true) {
            val token = readToken()
            if (token.type == TokenType.UNKNOWN) {
                log("Breaking out of parse loop")
                break
            } else {
                push(token)
            }
        }

        while (operators.isNotEmpty()) {
            applyOperator()
        }
        if (values.isEmpty()) {
            throw EvaluationException("No value found", 0)
        }
        if (values.size > 1) {
            throw EvaluationException("Does not evaluate to a single value", 0)
        } else {
            return values[0]
        }
    }

    /**
     * Adds a token to one of the two stacks.
     * If it is an identifier, such as a literal value, or a name of an object then it is
     * just added to the list of values.
     * If it is a method or property name, then the object is popped from values list, and replaced
     * with the corresponding "method" or "property". Note, for methods, the "(" token is treated as an operator,
     * and this operator, in conjunction with ")" operator will pop the "method" and replace it with the
     * implementation of that method.
     * If it is not an identifier, it must be an operator, such as "+", ".", "(" etc.
     * In this case, if the precedence of the previous operator (if there is one) is ??? then that previous
     * operator must first be applied. This involves popping the operator and its operands off the stacks, and
     * putting the result of the operator onto the value stack. Then continue processing the new operator as normal.
     * The operator is added to the operator stack, without further processing, because it may need future operators
     * to be applied first.
     *
     * When the end of the expression is reached, the special "EndToken" is pushed. This is the lowest precedence,
     * and therefore causes all of the operators on the stack to be evaluated.
     */
    private fun push(token: Token) {

        try {
            when (token.type) {
                TokenType.UNKNOWN -> return
                TokenType.NUMBER -> pushNumber(token)
                TokenType.IDENTIFIER -> pushIdentifier(token)
                TokenType.OPERATOR -> pushOperator(token)
                TokenType.STRING -> pushString(token)
            }
        } catch (e1: EvaluationException) {
            throw e1
        } catch (e2: Exception) {
            throw EvaluationException(e2, token.startIndex)
        }
    }

    private fun pushValue(value: Prop<*>) {
        log("Push value : $value")
        values.add(value)
        expectValue = false
    }

    private fun pushOperator(op: Operator) {
        log("Push $op")
        operators.add(op)
        expectValue = op.expectsValue()

    }

    private fun pushNumber(token: Token) {
        try {
            val number = token.toDouble()
            pushValue(DoubleValue(number))
        } catch (e: Exception) {
            throw EvaluationException("Not a valid number : ${token.text}", token.startIndex)
        }
    }

    private fun pushString(token: Token) {
        pushValue(StringValue(token.text))
    }


    private fun pushIdentifier(token: Token) {
        // TODO If the top operator is ".", then push an identifier, and apply the "."
        val function = Function.find(token.text)
        if (function == null) {
            val constant = constants.get(token.text)
            if (constant == null) {
                throw EvaluationException("Unknown identifier ${token.text}", token.startIndex)
            } else {
                pushValue(constant)
            }
        } else {
            pushValue(function)
        }
    }

    private fun pushOperator(token: Token) {
        var op = Operator.find(token.text) ?: throw EvaluationException("Unknown operator $text", token.startIndex)

        when (op) {
            is OpenBracketOperator -> {
                if (expectValue) {
                    pushOperator(op)
                } else {
                    val name = peekValue()
                    if (name is Function) {
                        pushOperator(Operator.APPLY)
                    } else {
                        throw EvaluationException("Expected function name before '('", token.startIndex)
                    }
                }
            }
            is CloseBracketOperator -> {
                log("Close bracket")
                while (operators.lastOrNull() !is OpenBracket) {
                    applyOperator()
                }

                val open = peekOperator()
                when (open) {
                    is OpenBracketOperator -> popOperator()
                    is ApplyOperator -> {
                        log("Applying function")
                        applyOperator()
                    }
                    else -> throw EvaluationException("Unmatched ')'", token.startIndex)
                }
            }

            else -> {
                if (expectValue) {
                    if (op.unaryOperator != null) {
                        op = op.unaryOperator!!
                        log("Using : Unary $op")
                    } else {
                        throw EvaluationException("Syntax error", token.startIndex)
                    }
                }

                while (peekOperator()?.precedence ?: -1 > op.precedence) {
                    log("Applying ${peekOperator()} as it is higher than $op")
                    applyOperator()
                }
                pushOperator(op)
            }
        }
    }

    private fun peekValue(): Prop<*>? = values.lastOrNull()

    private fun peekOperator(): Operator? = operators.lastOrNull()

    private fun popOperator(): Operator {
        return operators.removeAt(operators.size - 1)
    }

    private fun popValue(): Prop<*> {
        return values.removeAt(values.size - 1)
    }

    private fun applyOperator() {
        val op = popOperator()
        log("Applying $op")
        try {
            pushValue(op.apply(values))
        } catch (e: Exception) {
            throw EvaluationException(e, 0) // TODO Add the correct position.
        }
    }

    companion object {
        private val constants = mutableMapOf<String, Prop<*>>(
                "PI" to DoubleValue(Math.PI),
                "TAU" to DoubleValue(Math.PI * 2),
                "E" to DoubleValue(Math.E),
                "MAX_DOUBLE" to DoubleValue(Double.MAX_VALUE),
                "MIN_DOUBLE" to DoubleValue(-Double.MAX_VALUE), // Note, this is NOT the same as the badly named Java Double.MIN_VALUE
                "SMALLEST_DOUBLE" to DoubleValue(Double.MIN_VALUE)
        )
    }
}
