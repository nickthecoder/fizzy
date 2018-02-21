package uk.co.nickthecoder.fizzy.util

/**
 * The handler for [runLater]. The default is to run the actions immediately in the current thread.
 * When using JavaFX, this should be set using :
 *
 *    runLaterHandler = { Platform.runLater(it) }
 *
 */
var runLaterHandler: ((() -> Unit) -> Unit)? = { it() }

/**
 * Runs an action at a later time.
 * If no [runLaterHandler] has been set, then the action is ignored.
 */
fun runLater(action: () -> Unit) {
    runLaterHandler?.let {
        it(action)
    }
}
