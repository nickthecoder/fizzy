/*
Fizzy
Copyright (C) 2018 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.fizzy.gui

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.fizzy.Fizzy

/**
 */
open class ApplicationAction(
        val name: String,
        val label: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        val tooltip: String? = label
) {

    private val defaultKeyCodeCombination: KeyCodeCombination? =
            keyCode?.let { createKeyCodeCombination(keyCode, shift, control, alt, meta) }

    var keyCodeCombination = defaultKeyCodeCombination

    open val image: Image? = Fizzy.imageResource("icons/$name.png")

    fun revert() {
        keyCodeCombination = defaultKeyCodeCombination
    }

    fun isChanged(): Boolean = keyCodeCombination != defaultKeyCodeCombination

    fun shortcutString(): String = if (keyCodeCombination?.code == null) "" else keyCodeCombination.toString()

    fun match(event: KeyEvent): Boolean {
        return keyCodeCombination?.match(event) == true
    }

    fun createTooltip(): Tooltip? {
        if (tooltip == null && keyCodeCombination == null) {
            return null
        }

        val result = StringBuilder()
        tooltip?.let { result.append(it) }

        if (tooltip != null && keyCodeCombination != null) {
            result.append(" (")
        }
        keyCodeCombination?.let { result.append(it.displayText) }
        if (tooltip != null && keyCodeCombination != null) {
            result.append(")")
        }

        return Tooltip(result.toString())
    }

    fun shortcutLabel() = keyCodeCombination?.displayText

    fun createMenuItem(shortcuts: ShortcutHelper? = null, action: () -> Unit): MenuItem {
        shortcuts?.add(this, action)

        val menuItem = MenuItem(label)
        menuItem.onAction = EventHandler { action() }
        image?.let { menuItem.graphic = ImageView(it) }
        menuItem.accelerator = keyCodeCombination
        return menuItem
    }

    fun createButton(shortcutHelper: ShortcutHelper? = null, forceLabel: Boolean = false, action: () -> Unit): Button {

        shortcutHelper?.add(this, action)

        val button = Button()
        updateButton(button, forceLabel, action)
        return button
    }

    fun createToggleButton(shortcutHelper: ShortcutHelper? = null, forceLabel: Boolean = false, action: () -> Unit): ToggleButton {

        val button = ToggleButton()
        shortcutHelper?.add(this, {
            button.isSelected = !button.isSelected
            action()
        })

        updateButton(button, forceLabel, action)
        return button
    }

    fun create(shortcutHelper: ShortcutHelper? = null, shapePicker: ShapePicker): SplitMenuButton {
        val button = shapePicker.build()
        button.tooltip = createTooltip()
        shortcutHelper?.add(this, shapePicker::onAction)
        return button
    }

    private fun updateButton(button: ButtonBase, forceLabel: Boolean, action: () -> Unit) {
        if (image != null) {
            button.graphic = ImageView(image)
        }
        if (forceLabel || image == null) {
            button.text = label
        }
        button.onAction = EventHandler {
            action()
        }
        button.tooltip = createTooltip()
    }

    companion object {

        fun modifier(down: Boolean?) =
                if (down == null) {
                    KeyCombination.ModifierValue.ANY
                } else if (down) {
                    KeyCombination.ModifierValue.DOWN
                } else {
                    KeyCombination.ModifierValue.UP
                }

        fun createKeyCodeCombination(
                keyCode: KeyCode,
                shift: Boolean? = false,
                control: Boolean? = false,
                alt: Boolean? = false,
                meta: Boolean? = false) =

                KeyCodeCombination(
                        keyCode,
                        modifier(shift), modifier(control), modifier(alt), modifier(meta), modifier(false))
    }
}


