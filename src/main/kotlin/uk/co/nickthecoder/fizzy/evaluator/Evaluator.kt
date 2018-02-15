package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.prop.DoubleValue
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.StringValue

class Evaluator(val text: CharSequence) {

    var index = 0

    var operators = mutableListOf<Operator>()
    var values = mutableListOf<Prop<*>>()

    var debug = false

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
            log("Found $token")
            if (token.text.isBlank()) {
                log("Breaking out of parse loop")
                break
            } else {
                log("Pushing $token")
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


        when (token.type) {
            TokenType.UNKNOWN -> return
            TokenType.NUMBER -> pushNumber(token)
            TokenType.IDENTIFIER -> pushIdentifier(token)
            TokenType.OPERATOR -> pushOperator(token)
            TokenType.STRING -> pushString(token)
        }
    }

    private fun pushNumber(token: Token) {
        try {
            val number = token.toDouble()
            values.add(DoubleValue(number))
        } catch (e: Exception) {
            throw EvaluationException("Not a valid number : ${token.text}", token.startIndex)
        }
    }

    private fun pushString(token: Token) {
        values.add(StringValue(token.text))
    }

    private fun pushOperator(token: Token) {
        val op = Operators.find(token.text)?.op ?: throw EvaluationException("Unknown operator $text", token.startIndex)

        when (op) {
            is CloseBracketOperator -> {
                log("Close bracket")
                while (operators.lastOrNull() !is OpenBracketOperator) {
                    applyOperator()
                }
                if (operators.lastOrNull() !is OpenBracketOperator) {
                    throw EvaluationException("Unmatched ')'", token.startIndex)
                } else {
                    operators.removeAt(operators.size - 1)
                }
            }
            is OpenBracketOperator -> {
                operators.add(op)
            }
            else -> {

                while (operators.lastOrNull()?.precedence ?: -1 > op.precedence) {
                    applyOperator()
                }
                operators.add(op)
            }
        }
    }

    private fun applyOperator() {
        val op = operators.removeAt(operators.size - 1)
        log("Applying $op")
        values.add(op.apply(values))
    }

    private fun pushIdentifier(token: Token) {
        /*
        val tokenText = token.text
        val op = operators.lastOrNull()
        val v = values.lastOrNull()

        if (op.toString() == ".") {
            val propertyOrMethod = v.findPropertyOrMethod(tokenText)
            if (propertyOrMethod == null) {
                throw EvaluationException("Property or method $tokenText", token.startIndex)
            } else {
                popOp()
                popValue()
                values.add(propertyOrMethod)
            }
        } else {
            values.add(token)
        }
        */
    }

}

class EvaluationException(s: String, index: Int) : Exception(s)
