package uk.co.nickthecoder.fizzy.prop

interface StringProp : Prop<String>

class StringConstant(value: String = "")
    : StringProp, PropConstant<String>(value)

class StringPlus(a: Prop<String>, b: Prop<String>)
    : StringProp, BinaryPropCalculation<String>(a, b) {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}
