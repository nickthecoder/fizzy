package uk.co.nickthecoder.fizzy.prop

class StringPropType : PropType<String>(String::class) {

    override fun findField(prop: Prop<String>, name: String): PropField<String, *>? {
        return when (name) {
            "length" -> PropField<String, Double>(prop) { prop.value.length.toDouble() }
            else -> null
        }
    }

    override fun findMethod(prop: Prop<String>, name: String): PropMethod<String, *>? {
        return when (name) {
            "head" -> PropMethodDouble(prop) { a -> prop.value.substring(0, a.toInt()) }
            "tail" -> PropMethodDouble(prop) { a ->
                val l = prop.value.length
                prop.value.substring(l - a.toInt(), l)
            }
            "substring" -> PropMethodDoubleDouble(prop) { a, b -> prop.value.substring(a.toInt(), b.toInt()) }
            else -> null
        }
    }
}

class StringConstant(value: String = "")
    : PropConstant<String>(value)
