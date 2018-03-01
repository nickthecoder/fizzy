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
package uk.co.nickthecoder.fizzy.util

import junit.framework.TestCase
import uk.co.nickthecoder.fizzy.evaluator.EvaluationException
import java.lang.RuntimeException

abstract class MyTestCase : TestCase() {

    val tiny = 0.000001

    fun assertFails(action: () -> Any) {
        try {
            action()
        } catch (e: Exception) {
            return
        }
        throw RuntimeException("Expected an Exception")
    }

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