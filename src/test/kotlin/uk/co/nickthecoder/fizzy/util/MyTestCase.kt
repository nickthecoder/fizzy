package uk.co.nickthecoder.fizzy.util

import junit.framework.TestCase
import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import java.lang.RuntimeException

abstract class MyTestCase : TestCase() {

    val tiny = 0.000001

    fun assertFailsAt(position: Int, expression: () -> Any) {
        try {
            expression()
            throw RuntimeException("Expected an EvaluationException")
        } catch (e: EvaluationException) {
            if (position != e.index) {
                throw RuntimeException("Expected an EvaluationException at $position, but found one at ${e.index}", e)
            }
        }
    }

}