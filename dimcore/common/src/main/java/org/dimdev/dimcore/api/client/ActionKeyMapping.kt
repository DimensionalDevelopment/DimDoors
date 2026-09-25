package org.dimdev.dimcore.api.client

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.Screen
import java.util.function.BooleanSupplier

class ActionKeyMapping(name: String, type: InputConstants.Type, keyCode: Int, category: String, private val gate: () -> Boolean,private val action: () -> Unit) : KeyMapping(name, type, keyCode, category) {
    private var down = false

    constructor(name: String, keyCode: Int, category: String, action: () -> Unit) : this(
        name,
        InputConstants.Type.KEYSYM,
        keyCode,
        category,
        NONE,
        action
    )

    constructor(name: String, keyCode: Int, category: String, gate: () -> Boolean, action: () -> Unit) : this(
        name,
        InputConstants.Type.KEYSYM,
        keyCode,
        category,
        gate,
        action
    )

    override fun setDown(value: Boolean) {
        super.setDown(value)

        val was = this.down
        this.down = value

        if (value && !was && this.gate.invoke()) {
            this.action.invoke()
        }
    }

    override fun consumeClick(): Boolean {
        super.consumeClick()
        return false
    }

    companion object {
        private val NONE = { true }

        fun shift(name: String, keyCode: Int, category: String, action: () -> Unit): ActionKeyMapping = ActionKeyMapping(name, keyCode, category, Screen::hasShiftDown, action)
        fun control(name: String, keyCode: Int, category: String, action: () -> Unit): ActionKeyMapping = ActionKeyMapping(name, keyCode, category, Screen::hasControlDown, action)
        fun alt(name: String, keyCode: Int, category: String, action: () -> Unit): ActionKeyMapping = ActionKeyMapping(name, keyCode, category, Screen::hasAltDown, action)
    }
}
