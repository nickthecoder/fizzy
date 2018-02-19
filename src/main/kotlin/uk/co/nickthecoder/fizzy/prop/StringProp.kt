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
            "head" -> PropMethod1(prop, Double::class) { a -> prop.value.substring(0, a.toInt()) }
            "tail" -> PropMethod1(prop, Double::class) { a ->
                val l = prop.value.length
                prop.value.substring(l - a.toInt(), l)
            }
            "substring" -> PropMethod2(prop, Double::class, Double::class) { a, b -> prop.value.substring(a.toInt(), b.toInt()) }
            else -> null
        }
    }
}

class StringConstant(value: String = "")
    : PropConstant<String>(value)
