package uk.co.nickthecoder.fizzy.prop

import uk.co.nickthecoder.fizzy.model.Dimension
import uk.co.nickthecoder.fizzy.model.Dimension2
import uk.co.nickthecoder.fizzy.model.Vector2
import kotlin.reflect.KClass

/**
 * See [DummyPropType]
 */
class Dummy

/**
 * Functions are implemented by the same mechanism as methods, where the [Prop] that the methods refers to is of type
 * Prop<[Dummy]> (and is is just ignored!
 */
class DummyPropType private constructor()
    : PropType<Dummy>(Dummy::class) {

    override fun findField(prop: Prop<Dummy>, name: String): PropField<Dummy, *>? {
        return null
    }

    override fun findMethod(prop: Prop<Dummy>, name: String): PropMethod<Dummy, *>? {
        return when (name) {
            "abs" -> PropFunction1(Double::class) { Math.abs(it) }
            "ceil" -> PropFunction1(Double::class) { Math.ceil(it) }
            "floor" -> PropFunction1(Double::class) { Math.floor(it) }
            "exp" -> PropFunction1(Double::class) { Math.exp(it) }
            "ln" -> PropFunction1(Double::class) { Math.log(it) }
            "log" -> PropFunction1(Double::class) { Math.log10(it) }
            "sqrt" -> PropFunction1(Double::class) { Math.sqrt(it) }

            "Vector2" -> PropFunction2(Double::class, Double::class) { x, y -> Vector2(x, y) }
            "Dimension2" -> PropFunction2(Dimension::class, Dimension::class) { x, y -> Dimension2(x, y) }
            else -> null
        }
    }

    companion object {
        val instance = DummyPropType()
    }
}

/**
 * The one and only instance of a [DummyConstant] used as the [PropMethod]'s value when the 'method' is really a
 * function (and applies to nothing).
 */
val dummyInstance = PropConstant(Dummy())

class PropFunction1<A : Any, R : Any>(klassA: KClass<A>, lambda: (A) -> R)
    : PropMethod1<Dummy, A, R>(dummyInstance, klassA, lambda)

class PropFunction2<A : Any, B : Any, R : Any>(klassA: KClass<A>, klassB: KClass<B>, lambda: (A, B) -> R)
    : PropMethod2<Dummy, A, B, R>(dummyInstance, klassA, klassB, lambda)