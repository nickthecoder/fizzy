package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.StrokeCap

class StrokeCapExpression
    : PropExpression<StrokeCap> {

    constructor(formula: String) : super(formula, StrokeCap::class.java)

    constructor(other: StrokeCapExpression) : super(other)


    override val defaultValue = StrokeCap.BUTT

    override fun copy(link: Boolean) = if (link) StrokeCapExpression(this) else StrokeCapExpression(formula)

    override fun valueString() = value.toFormula()

}
