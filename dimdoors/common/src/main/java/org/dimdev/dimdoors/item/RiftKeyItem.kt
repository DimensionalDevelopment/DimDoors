package org.dimdev.dimdoors.item

import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget
import org.dimdev.dimdoors.rift.targets.TempTarget
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.sound.ModSoundEvents

class RiftKeyItem(settings: Properties) : Item(
    settings.durability(10).component(ModDataComponentTypes.VIRTUAL_TARGET, PrivatePocketTarget)
) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level

        if (context.level.isClientSide()) {
            return InteractionResult.SUCCESS
        }

        if (context.player!!.isCrouching) {
            val stack = context.itemInHand
            val temp = stack.get(ModDataComponentTypes.VIRTUAL_TARGET)

            if (temp == null || temp === VirtualTarget.NoneTarget) {
                return InteractionResult.FAIL
            }

            val pos = context.clickedPos
            val state = level.getBlockState(pos)

            val provider = state.block.castOrNull<RiftVariantProvider>() ?: return super.useOn(context)

            val rift = provider.convertToRiftProvider(level as ServerLevel, pos, state) ?: return super.useOn(context)

            val original: VirtualTarget<*> = rift.data.destination
            rift.setDestination(TempTarget(temp.copy(), original.copy()))
            context.level.playSound(null, rift.riftBlockPos, ModSoundEvents.KEY_LOCK, SoundSource.BLOCKS)

            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }
}
