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

class TestColor : MyTestCase() {

    fun eval(string: String) = Evaluator(string).parse().value

    @Test
    fun testColor() {
        assertEquals(0.5, eval("RGB(0.5, 0.6, 0.7).Red"))
        assertEquals(0.6, eval("RGB(0.5, 0.6, 0.7).Green"))
        assertEquals(0.7, eval("RGB(0.5, 0.6, 0.7).Blue"))

        assertEquals(0.8, eval("RGBA(0.5, 0.6, 0.7, 0.8).Opacity"))
        assertEquals(0.8, eval("RGBA(0.5, 0.6, 0.7, 0.8).Alpha"))

        assertEquals(0.0, eval("RGB(1, 0, 0).Hue"))
        assertEquals(120.0, eval("RGB(0, 1, 0).Hue"))
        assertEquals(240.0, eval("RGB(0, 0, 1).Hue"))

        assertEquals(1.0, eval("RGB(1,1,1).Brightness"))
        assertEquals(1.0, eval("RGB(1,0,1).Brightness"))
        assertEquals(0.5, eval("RGB(0.5,0,0.5).Brightness"))

        assertEquals(1.0, eval("RGB(1,0,0).Saturation"))
        assertEquals(1.0, eval("RGB(0.5,0,0.5).Saturation"))
        assertEquals(0.5, eval("RGB(0.5,0.25,0.5).Saturation"))

        assertEquals(120.0, eval("HSB(120, 0.4,0.6).Hue"))
        assertEquals(0.4, eval("HSB(120, 0.4,0.6).Saturation"))
        assertEquals(0.6, eval("HSB(120, 0.4,0.6).Brightness"))

    }

}
