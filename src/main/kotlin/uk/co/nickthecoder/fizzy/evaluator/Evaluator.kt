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
package uk.co.nickthecoder.fizzy.evaluator

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.prop.*

/**
 * Parses a expression (a [CharSequence]), converting it into a single [Prop].
 *
 * Note that parsing is NOT the same as evaluating, and therefore parsing "2+2" does NOT return 4.
 * Instead, it will return a [DoublePlus] object. It will only be evaluated when [Prop.value] is first referenced.
 * The value 4 will then be cached by the [DoublePlus] object so that subsequent references to [Prop.value] will
 * NOT re-evaluated. If either of [DoublePlus]'s arguments are changed (or made 'dirty'), then referencing
 * [Prop.value] will cause a re-evaluation. In this simple example of "2+2", this won't happen because "2" is a constant,
 * and therefore the [DoublePlus] will never need re-evaluating.
 *
 * During parsing, there are two stacks, the [values] stack and the [operators] stack. As parsing progresses
 * an operator and its corresponding values are popped off the stacks, and the result of the operation is pushed onto
 * the [values] stack. At the end of the parsing, the [operators] stacks should both be empty, and the [values] stack
 * should contain a single value, which is returned from [parse].
 */
class Evaluator(val text: CharSequence, val context: EvaluationContext = constantsContext) {

    private var index = 0

    private var operators = mutableListOf<Token>()
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

    private fun unRead(chars: Int) {
        index -= chars
    }

    fun parse(): Prop<*> {
        log("Parsing text $text")
        while (true) {
            var token = readToken()

            // Special case for infix operators, which do NOT use symbols, such as xor
            if (!expectValue && Operator.find(token.text) != null) {
                token.type = TokenType.OPERATOR
            }

            if (token.type == TokenType.OPERATOR && Operator.find(token.text) == null) {
                // This operator isn't know, so take chars off the end till it IS known, or we run out of chars

                var text = token.text
                while (text.isNotBlank()) {
                    text = text.dropLast(1)
                    if (Operator.find(text) != null) {
                        break
                    }
                }
                unRead(token.text.length - text.length)
                if (text.isBlank()) {
                    throw EvaluationException("Unknown operator ${token.text}", token.startIndex)
                }
                token = Token(token.startIndex, text, TokenType.OPERATOR)
            }

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

        log("-------------")

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

    private fun pushOperator(token: Token, op: Operator) {
        log("Push $token")
        token.operator = op
        operators.add(token)
        expectValue = token.operator.expectsValue()

    }

    private fun pushOperator(token: Token) {
        var op = Operator.find(token.text) ?: throw EvaluationException("Unknown operator ${token.text}", token.startIndex)

        when (op) {
            is OpenBracket -> {
                if (expectValue) {
                    log("Pushing Simple (")
                    pushOperator(token, op)
                } else {
                    if (peekOperator()?.operator is DotOperator && peekValue() is FieldOrMethodName) {
                        popOperator()
                        val methodName = (popValue() as FieldOrMethodName).value
                        if (values.size == 0) {
                            println("### B2")

                            // I don't think this can ever happen.
                            throw EvaluationException("No value to apply method $methodName", token.startIndex)
                        }
                        val prop = popValue()
                        log("Looking for method $methodName for $prop")
                        val method = PropType.method(prop, methodName) ?:
                                throw EvaluationException("Couldn't find method $methodName", token.startIndex)

                        pushValue(method)
                        log("Pushing Apply for method")
                        pushOperator(token, Operator.APPLY)

                    } else if (peekValue() is PropMethod<*, *>) {
                        log("Pushing APPLY for function call")
                        pushOperator(token, Operator.APPLY)

                    } else {
                        throw EvaluationException("( not expected", token.startIndex)
                    }
                }
            }
            is CloseBracketOperator -> {
                log("Close bracket. ExpectValue ? ${expectValue}")
                // If we have : foo(), then no value has been pushed on the stack after the "(", so lets add
                // an empty argument list.
                if (expectValue) {
                    log("Adding empty argument list")
                    pushValue(ArgList())
                }

                val open = lookingForOpenBracket(token)
                when (open) {
                    is OpenBracketOperator -> popOperator()
                    is ApplyOperator -> {
                        log("Applying method/function")
                        applyOperator()
                    }
                    else -> throw EvaluationException("Unmatched ')'", token.startIndex)
                }
            }

            else -> {
                if (expectValue) {
                    if (op.prefixOperator != null) {
                        op = op.prefixOperator!!
                        log("Using : Unary $op")
                    } else {
                        throw EvaluationException("Syntax error", token.startIndex)
                    }
                }

                while (peekOperator()?.operator?.precedence ?: -1 >= op.precedence) {
                    log("Applying ${peekOperator()} as it is higher than $op")
                    applyOperator()
                }
                pushOperator(token, op)
            }
        }
    }

    /**
     * Applies any non-open brackets on the stack.
     * If an open bracket is not found, then an expection is throw.
     * Otherwise the open bracket is returned, but it is NOT popped off the stack, and the top-most item on the
     * stack will be the open bracket.
     */
    private fun lookingForOpenBracket(close: Token): Operator {
        var op = peekOperator()
        do {
            if (op == null) {
                throw EvaluationException("Unmatched close bracket", close.startIndex)
            }
            if (op.operator is OpenBracket) {
                return op.operator
            }
            applyOperator()
            op = peekOperator()
        } while (true)
    }

    private fun pushNumber(token: Token) {
        try {
            val number = token.toDouble()
            pushValue(PropConstant(number))
        } catch (e: Exception) {
            throw EvaluationException("Not a valid number : ${token.text}", token.startIndex)
        }
    }

    private fun pushString(token: Token) {
        pushValue(PropConstant(token.text))
    }

    private fun pushIdentifier(token: Token) {
        if (expectValue) {

            if (peekOperator()?.operator is DotOperator) {
                log("Field of method name : ${token.text}")
                pushValue(FieldOrMethodName(token.text))

            } else {
                val property = context.findProp(token.text)
                if (property == null) {

                    val func = PropType.method(dummyInstance, token.text)
                    if (func == null) {
                        throw EvaluationException("Identifier ${token.text} not found.", token.startIndex)
                    } else {
                        pushValue(func)
                    }
                } else {
                    pushValue(property)
                }
            }
        } else {
            val conversion = Conversions.find(token.text)
                    ?: throw EvaluationException("Expected an operator or conversion", token.startIndex)
            log("Converting ${token.text}")
            val value = popValue()
            val converted = conversion.invoke(value)
            pushValue(converted)
        }
    }

    private fun peekValue(): Prop<*>? = values.lastOrNull()

    private fun peekOperator(): Token? = operators.lastOrNull()

    private fun popOperator(): Token {
        return operators.removeAt(operators.size - 1)
    }

    private fun popValue(): Prop<*> {
        return values.removeAt(values.size - 1)
    }

    private fun applyOperator() {
        val opToken = popOperator()
        val op = opToken.operator
        log("Applying $op")
        try {
            pushValue(op.apply(values))
        } catch (e: Exception) {
            throw EvaluationException(e, opToken.startIndex)
        }
    }

    companion object {
        init {
            PropType.put(AnglePropType.instance)
            PropType.put(Dimension2PropType.instance)
            PropType.put(Dimension2PropType.instance)
            PropType.put(DoublePropType.instance)
            PropType.put(DummyPropType.instance)
            PropType.put(FListPropType.instance)
            PropType.put(FListPropType.instance, MutableFList::class)
            PropType.put(GeometryPropType.instance)
            PropType.put(LineToPropType.instance)
            PropType.put(MoveToPropType.instance)
            PropType.put(Shape1dPropType.instance)
            PropType.put(Shape2dPropType.instance)
            PropType.put(ShapeGroupPropType.instance)
            PropType.put(StringPropType.instance)
            PropType.put(Vector2PropType.instance)
        }
    }
}
