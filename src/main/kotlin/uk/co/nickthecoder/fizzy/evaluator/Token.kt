package uk.co.nickthecoder.fizzy.evaluator


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
