package uk.co.nickthecoder.fizzy.evaluator

class EvaluationException : Exception {

    val index: Int

    constructor(message: String, index: Int) : super(message) {
        this.index = index
    }

    constructor(e: Exception, index: Int) : super(e) {
        this.index = index
    }

    override fun toString() = "$message @ $index"
}
