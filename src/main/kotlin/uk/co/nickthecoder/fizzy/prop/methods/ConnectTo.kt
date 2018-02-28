package uk.co.nickthecoder.fizzy.prop.methods

import uk.co.nickthecoder.fizzy.model.ConnectionPoint
import uk.co.nickthecoder.fizzy.model.Shape
import uk.co.nickthecoder.fizzy.prop.Prop
import uk.co.nickthecoder.fizzy.prop.PropMethod1

class ConnectTo(prop: Prop<Shape>)
    : PropMethod1<Shape, ConnectionPoint>(prop, ConnectionPoint::class, { connectionPoint ->

    val otherShape = connectionPoint.shape ?: throw RuntimeException("ConnectionPoint does not have a Shape")
    prop.value.parent.fromPageToLocal.value * otherShape.fromLocalToPage.value * connectionPoint.point.value

})
