package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.StrokeJoin

class StrokeJoinExpression
    : PropExpression<StrokeJoin> {

    constructor(formula: String) : super(formula, StrokeJoin::class.java)

    constructor(other: StrokeJoinExpression) : super(other)


    override val defaultValue = StrokeJoin.BEVEL

    override fun copy(link: Boolean) = if (link) StrokeJoinExpression(this) else StrokeJoinExpression(formula)

    override fun valueString() = value.toFormula()

}
