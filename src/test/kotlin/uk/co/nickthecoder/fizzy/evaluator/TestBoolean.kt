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

import org.junit.Test
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestBoolean : MyTestCase() {

    fun parseBoolean(formula: String) = Evaluator(formula).parse().value as Boolean

    @Test
    fun testConstants() {
        val t = parseBoolean("true")
        assertEquals(true, t)

        val f = parseBoolean("false")
        assertEquals(false, f)
    }

    @Test
    fun testNot() {
        val nt = parseBoolean("!true")
        assertEquals(false, nt)

        val nf = parseBoolean("!false")
        assertEquals(true, nf)
    }

    @Test
    fun testOr() {
        assertEquals(true, parseBoolean("true || true"))
        assertEquals(true, parseBoolean("true || false"))
        assertEquals(true, parseBoolean("false || true"))
        assertEquals(false, parseBoolean("false || false"))
    }

    @Test
    fun testAnd() {
        assertEquals(true, parseBoolean("true && true"))
        assertEquals(false, parseBoolean("true && false"))
        assertEquals(false, parseBoolean("false && true"))
        assertEquals(false, parseBoolean("false && false"))
    }

    @Test
    fun testXor() {
        assertEquals(false, parseBoolean("true xor true"))
        assertEquals(true, parseBoolean("true xor false"))
        assertEquals(true, parseBoolean("false xor true"))
        assertEquals(false, parseBoolean("false xor false"))
    }

    @Test
    fun testPrecedence() {
        assertEquals(false, parseBoolean("!(true || true)"))
        assertEquals(true, parseBoolean("!true || true"))

        assertEquals(true, parseBoolean("!(true && false)"))
        assertEquals(false, parseBoolean("!true && false"))

        assertEquals(true, parseBoolean("false && true || true"))
        assertEquals(true, parseBoolean("(false && true) || true")) // No change
        assertEquals(false, parseBoolean("false && (true || true)")) // Change!

        assertEquals(1.0, 1.0, 1.0)
    }

    @Test
    fun testDouble() {
        assertEquals(true, parseBoolean("1+1 == 2"))
        assertEquals(false, parseBoolean("1+1 == 1"))

        assertEquals(false, parseBoolean("1+1 != 2"))
        assertEquals(true, parseBoolean("1+1 != 1"))

        assertEquals(false, parseBoolean("1 > 2"))
        assertEquals(false, parseBoolean("2 > 2"))
        assertEquals(true, parseBoolean("3 > 2"))

        assertEquals(false, parseBoolean("1 >= 2"))
        assertEquals(true, parseBoolean("2 >= 2"))
        assertEquals(true, parseBoolean("3 >= 2"))

        assertEquals(true, parseBoolean("1 < 2"))
        assertEquals(false, parseBoolean("2 < 2"))
        assertEquals(false, parseBoolean("3 < 2"))

        assertEquals(true, parseBoolean("1 <= 2"))
        assertEquals(true, parseBoolean("2 <= 2"))
        assertEquals(false, parseBoolean("3 <= 2"))
    }

    @Test
    fun testDimension() {
        assertEquals(true, parseBoolean("(10mm) == (10mm)"))
        assertEquals(false, parseBoolean("(10mm) != (10mm)"))

        assertEquals(true, parseBoolean("10mm == 10mm"))
        assertEquals(false, parseBoolean("10mm != 10mm"))

        assertEquals(true, parseBoolean("1cm == 10mm"))
        assertEquals(false, parseBoolean("1cm != 10mm"))

        assertEquals(false, parseBoolean("10cm == 10mm"))
        assertEquals(true, parseBoolean("10cm != 10mm"))

        assertEquals(false, parseBoolean("10mm == 11mm"))
        assertEquals(true, parseBoolean("10mm != 11mm"))


        assertEquals(false, parseBoolean("1m > 2m"))
        assertEquals(false, parseBoolean("2m > 2m"))
        assertEquals(true, parseBoolean("3m > 2m"))

        assertEquals(false, parseBoolean("1m >= 2m"))
        assertEquals(true, parseBoolean("2m >= 2m"))
        assertEquals(true, parseBoolean("3m >= 2m"))

        assertEquals(true, parseBoolean("1m < 2m"))
        assertEquals(false, parseBoolean("2m < 2m"))
        assertEquals(false, parseBoolean("3m < 2m"))

        assertEquals(true, parseBoolean("1m <= 2m"))
        assertEquals(true, parseBoolean("2m <= 2m"))
        assertEquals(false, parseBoolean("3m <= 2m"))
    }

    @Test
    fun testAngle() {
        assertEquals(true, parseBoolean("45 deg == 45 deg"))
        assertEquals(false, parseBoolean("45 deg != 45 deg"))

        assertEquals(false, parseBoolean("45 rad == 45 deg"))
        assertEquals(true, parseBoolean("45 rad != 45 deg"))

        assertEquals(false, parseBoolean("45 deg == 46 deg"))
        assertEquals(true, parseBoolean("45 deg != 46 deg"))

        assertEquals(false, parseBoolean("1deg > 2deg"))
        assertEquals(false, parseBoolean("2deg > 2deg"))
        assertEquals(true, parseBoolean("3deg > 2deg"))

        assertEquals(false, parseBoolean("1deg >= 2deg"))
        assertEquals(true, parseBoolean("2deg >= 2deg"))
        assertEquals(true, parseBoolean("3deg >= 2deg"))

        assertEquals(true, parseBoolean("1deg < 2deg"))
        assertEquals(false, parseBoolean("2deg < 2deg"))
        assertEquals(false, parseBoolean("3 deg< 2deg"))

        assertEquals(true, parseBoolean("1deg <= 2deg"))
        assertEquals(true, parseBoolean("2deg <= 2deg"))
        assertEquals(false, parseBoolean("3deg <= 2deg"))

    }

    @Test
    fun testDimension2() {
        assertEquals(true, parseBoolean("Dimension2(10mm, 12mm) == Dimension2(10mm, 12mm)"))
        assertEquals(false, parseBoolean("Dimension2(10mm, 12mm) != Dimension2(10mm, 12mm)"))

        assertEquals(false, parseBoolean("Dimension2(10mm, 12mm) == Dimension2(10mm, 10mm)"))
        assertEquals(true, parseBoolean("Dimension2(10mm, 12mm) != Dimension2(12mm, 12mm)"))
    }

    @Test
    fun testVector2() {
        assertEquals(true, parseBoolean("Vector2(10, 12) == Vector2(10, 12)"))
        assertEquals(false, parseBoolean("Vector2(10, 12) != Vector2(10, 12)"))

        assertEquals(false, parseBoolean("Vector2(10, 12) == Vector2(10, 10)"))
    }

    @Test
    fun testIf() {
        assertEquals(3.0, Evaluator("if( true, 3, 2 )").parse().value)
        assertEquals(2.0, Evaluator("if( false, 3, 2 )").parse().value)
    }

}
