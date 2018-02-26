package uk.co.nickthecoder.fizzy.gui

import javafx.scene.Node

/**
 * Note. This is NOT a subclass of [Node], instead it call [build] ONCE to obtain the [Node] to add to the scene.
 * This allows the implementation to be hidden (and also makes development easier, because intellisense will not be
 * polluted by Node's methods).
 */

interface BuildableNode {
    fun build(): Node
}