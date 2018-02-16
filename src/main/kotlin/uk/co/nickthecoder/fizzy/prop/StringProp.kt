package uk.co.nickthecoder.fizzy.prop

abstract class StringProp(initialValue: String = "") : PropValue<String>(initialValue)

class StringValue(initialValue: String = "") : StringProp(initialValue)

class StringPlus(a: Prop<String>, b: Prop<String>) : BinaryPropCalculation<String>(a, b, "") {

    override fun eval() {
        calculatedValue = a.value + b.value
    }
}
