package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.prop.PropExpression

data class MetaData(

        val cellName: String,
        val cellExpression: PropExpression<*>,
        val sectionName: String? = null,
        val sectionIndex: Int? = null,
        val rowIndex: Int? = null
) {

    fun accessString(): String {
        val result = StringBuffer()
        sectionName?.let { result.append(sectionName) }
        sectionIndex?.let { result.append("$it") }
        sectionName?.let { result.append(".") }
        result.append(cellName)
        return result.toString()
    }

    override fun toString(): String = "${accessString()} : ${cellExpression.formula} = ${cellExpression.valueString()}"

}