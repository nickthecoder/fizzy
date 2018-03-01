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
