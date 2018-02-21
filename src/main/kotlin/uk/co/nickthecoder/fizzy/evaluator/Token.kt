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

/**
 * A [Token] is a consecutive sequence of characters from the string being parsed by the [Evaluator].
 * There are different types of Token (see [TokenType]).
 *
 * Identifiers start with a letter of an underscore, and can contain letters, underscores and digits.
 *
 * Operators contain symbols only, such as .()+-*
 *
 * Strings are and characters within double quotes. Strings can also contain escaped characters using the
 * backslash. e.g. \n is newline \" is a literal double quote, \t is a tab character.
 */
class Token(val startIndex: Int) {

    private val buffer = StringBuffer()
    var type = TokenType.UNKNOWN
    var ended: Boolean = false
    var literal: Boolean = false

    val text: String
        get() = buffer.toString()

    var operator: Operator = Operator.NONE

    fun accept(c: Char): Boolean {

        if (type != TokenType.STRING && c.isWhitespace()) return false

        when (type) {
            TokenType.UNKNOWN -> {
                if (c.isDigit()) {
                    type = TokenType.NUMBER
                    return doAccept(c, true)
                } else if (c == '"') {
                    type = TokenType.STRING
                    return true
                } else if (c.isJavaIdentifierStart()) {
                    type = TokenType.IDENTIFIER
                    return doAccept(c, true)
                } else {
                    type = TokenType.OPERATOR
                    return doAccept(c, Operator.isValid(c.toString()))
                }
            }
            TokenType.STRING -> {
                if (ended) {
                    return false
                } else if (literal) {
                    literal = false
                    return when (c) {
                        'n' -> doAccept('\n', true)
                        't' -> doAccept('\t', true)
                        else -> doAccept(c, true)
                    }
                } else {
                    if (c == '"') {
                        ended = true
                        return true
                    } else if (c == '\\') {
                        literal = true
                        return true
                    } else {
                        return doAccept(c, true)
                    }
                }
            }
            TokenType.NUMBER -> return doAccept(c, c.isDigit() || c == '.')
            TokenType.IDENTIFIER -> return doAccept(c, c.isJavaIdentifierPart())
            TokenType.OPERATOR -> return doAccept(c, Operator.isValid(text + c))
        }
    }

    private fun doAccept(c: Char, accept: Boolean): Boolean {
        if (accept) {
            buffer.append(c)
        }
        return accept
    }

    fun toDouble(): Double {
        try {
            return text.toDouble()
        } catch (e: Exception) {
            throw EvaluationException("Expected a number, but found $text", startIndex)
        }
    }

    override fun toString(): String = "Token $type : '$text'"
}
