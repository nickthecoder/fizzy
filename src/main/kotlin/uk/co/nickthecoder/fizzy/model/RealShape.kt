package uk.co.nickthecoder.fizzy.model

import uk.co.nickthecoder.fizzy.collection.MutableFList
import uk.co.nickthecoder.fizzy.prop.DimensionExpression
import uk.co.nickthecoder.fizzy.util.ChangeAndCollectionListener


/**
 * The basis for Shape1d and Shape2d, i.e. the type of Shapes which have Geometries, ConnectionPoints etc.
 */
abstract class RealShape(parent: Parent) : Shape(parent) {

    val geometries = MutableFList<Geometry>()

    val connectionPoints = MutableFList<ConnectionPoint>()

    val lineWidth = DimensionExpression("2mm")


    private val geometriesListener by lazy {
        // lazy to prevent leaking this in the constructor.
        // NOTE. I tried just creating this in postInit (without a val), and I got a failed unit test
        // This only happened from within IntelliJ (running directly from gradle, all tests passed).
        // It also passed when running the single Test class (and also a single method).
        // However, I'm not in the mood for a bug hunt, so I'll leave this implementation here.
        // It is slightly weird looking, but it works!
        ChangeAndCollectionListener(this, geometries,
                onAdded = { geometry -> geometry.shape = this@RealShape },
                onRemoved = { geometry -> geometry.shape = null }
        )
    }

    override fun isAt(point: Dimension2): Boolean {
        val localPoint = transform.fromParentToLocal.value * point

        geometries.forEach { geo ->
            if (geo.isAt(localPoint, lineWidth.value)) {
                return true
            }
        }

        return super.isAt(point)
    }

    override fun postInit() {
        listenTo(lineWidth)
        geometriesListener // Force it to be initialised (it is by lazy).
        super.postInit()


        collectionListeners.add(
                ChangeAndCollectionListener(this, connectionPoints,
                        onAdded = { cp -> cp.shape = this@RealShape },
                        onRemoved = { cp -> cp.shape = null }
                ))
    }

}
