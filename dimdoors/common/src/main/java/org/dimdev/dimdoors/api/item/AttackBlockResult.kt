package org.dimdev.dimdoors.api.item

import net.minecraft.world.InteractionResult

@JvmRecord
data class AttackBlockResult(@JvmField val result: InteractionResult?, @JvmField val sendPacket: Boolean) {
    companion object {
        @JvmStatic
        fun success(sendPacket: Boolean): AttackBlockResult {
            return AttackBlockResult(InteractionResult.SUCCESS, sendPacket)
        }

        @JvmStatic
        fun fail(sendPacket: Boolean): AttackBlockResult {
            return AttackBlockResult(InteractionResult.FAIL, sendPacket)
        }
    }
}
