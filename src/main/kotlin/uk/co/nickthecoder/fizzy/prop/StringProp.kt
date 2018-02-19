package uk.co.nickthecoder.fizzy.prop

class StringPropType : PropType<String>(String::class) {

    override fun findField(prop: Prop<String>, name: String): Prop<*>? {
        return when (name) {
            "length" -> PropField<String, Double>(prop) { prop.value.length.toDouble() }
            else -> null
        }
    }
}

class StringConstant(value: String = "")
    : PropConstant<String>(value)

class StringPlus(a: Prop<String>, b: Prop<String>)
    : BinaryPropCalculation<String>(a, b) {

    override fun eval() = a.value + b.value
}
