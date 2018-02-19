package uk.co.nickthecoder.fizzy.evaluator

/**
 * [Evaluator] should only throw [EvaluationException], which includes the position of the error, which makes
 * debugging easier.
 */
class EvaluationException : Exception {

    /**
     * A zero based index of the position of the error. The GUI will probably add one to this value to make it
     * more user-friendly.
     */
    val index: Int

    constructor(message: String, index: Int) : super(message) {
        this.index = index
    }

    constructor(e: Exception, index: Int) : super(e) {
        this.index = index
    }

    override fun toString() = "$message @ $index"
}
