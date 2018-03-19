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
package uk.co.nickthecoder.fizzy.model

import org.junit.Test
import uk.co.nickthecoder.fizzy.util.MyTestCase

class TestShapeTransform : MyTestCase() {

    @Test
    fun testTranslate() {
        val doc = Document()
        val page = Page(doc)
        doc.pages.add(page)

        val box = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(15mm,25mm)")
        var localP = Dimension2.ZERO_mm
        var pageP = box.fromLocalToPage.value * localP
        var localP2 = box.fromPageToLocal.value * pageP

        assertEquals(10.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(20.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(12.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(23.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)
    }

    @Test
    fun testRotate() {
        val doc = Document()
        val page = Page(doc)
        doc.pages.add(page)

        val box = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(15mm,25mm)")
        box.transform.locPin.formula = Dimension2.ZERO_mm.toFormula()

        var localP = Dimension2.ZERO_mm
        var pageP = box.fromLocalToPage.value * localP
        var localP2 = box.fromPageToLocal.value * pageP

        assertEquals(15.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(25.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(17.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(28.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)

        // Rotate by 90 degrees.
        box.transform.rotation.formula = Angle.degrees(90.0).toFormula()

        // This shouldn't affect the locPin location
        localP = Dimension2.ZERO_mm
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(15.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(25.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        // But the rotation should affect these ...

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(12.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(27.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)

    }

    /**
     * This is the same test as testRotate, but this time the center of rotation is about the CENTER of the box
     */
    @Test
    fun testRotate2() {
        val doc = Document()
        val page = Page(doc)
        doc.pages.add(page)

        val box = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(15mm,25mm)")

        var localP = Dimension2.ZERO_mm
        var pageP = box.fromLocalToPage.value * localP
        var localP2 = box.fromPageToLocal.value * pageP

        assertEquals(10.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(20.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(12.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(23.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)

        // Rotate by 90 degrees.
        box.transform.rotation.formula = Angle.degrees(90.0).toFormula()

        localP = Dimension2.ZERO_mm
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(20.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(20.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        // But the rotation should affect these ...

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box.fromLocalToPage.value * localP
        localP2 = box.fromPageToLocal.value * pageP

        assertEquals(17.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(22.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)

    }

    @Test
    fun testBoxInBox() {

        val doc = Document()
        val page = Page(doc)
        doc.pages.add(page)

        val box1 = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(15mm,25mm)")
        val box2 = createBox(box1, "Dimension2(6mm,4mm)", "Dimension2(2mm,4mm)")
        // box1 is centered at 15,25. Therefore bottom left is at 10,20
        // box2 is centered at 12,24. Therefore bottom left is at 9, 22

        var localP = Dimension2.ZERO_mm
        var pageP = box2.fromLocalToPage.value * localP
        var localP2 = box2.fromPageToLocal.value * pageP

        assertEquals(9.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(22.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box2.fromLocalToPage.value * localP
        localP2 = box2.fromPageToLocal.value * pageP

        assertEquals(11.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(25.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)
    }

    @Test
    fun testBoxInBoxRotated() {

        val doc = Document()
        val page = Page(doc)
        doc.pages.add(page)

        val box1 = createBox(page, "Dimension2(10mm,10mm)", "Dimension2(15mm,25mm)")
        val box2 = createBox(box1, "Dimension2(6mm,4mm)", "Dimension2(2mm,4mm)")
        box2.transform.rotation.formula = Angle.degrees(90.0).toFormula()
        // box1 is centered at 15,25. Therefore bottom left is at 10,20
        // box2 is centered at 12,24. Therefore bottom left is at 14,21 after rotation.

        var localP = Dimension2.ZERO_mm
        var pageP = box2.fromLocalToPage.value * localP
        var localP2 = box2.fromPageToLocal.value * pageP

        //println("Parent to Local\n ${box2.fromParentToLocal.value}")
        //println("Page to Local\n ${box2.fromPageToLocal.value}")
        //println("Local to Parent\n ${box2.fromLocalToParent.value}")
        //println("Local to Page\n ${box2.fromLocalToPage.value}")

        assertEquals(14.0, pageP.x.mm, tiny)
        assertEquals(0.0, localP2.x.mm, tiny)

        assertEquals(21.0, pageP.y.mm, tiny)
        assertEquals(0.0, localP2.y.mm, tiny)

        localP = Dimension2(Dimension(2.0, Dimension.Units.mm), Dimension(3.0, Dimension.Units.mm))
        pageP = box2.fromLocalToPage.value * localP
        localP2 = box2.fromPageToLocal.value * pageP

        assertEquals(11.0, pageP.x.mm, tiny)
        assertEquals(2.0, localP2.x.mm, tiny)

        assertEquals(23.0, pageP.y.mm, tiny)
        assertEquals(3.0, localP2.y.mm, tiny)
    }

}
