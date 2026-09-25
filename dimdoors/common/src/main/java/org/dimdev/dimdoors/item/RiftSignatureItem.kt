package org.dimdev.dimdoors.item

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimdoors.ModGameRules
import org.dimdev.dimdoors.api.item.has
import org.dimdev.dimdoors.api.util.RotatedLocation
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.RiftUtils
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.util.LevelSpaceHelper
import org.dimdev.dimdoors.world.ModDimensions
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

open class RiftSignatureItem(settings: Properties, var shouldclear: Boolean) : Item(settings) {
    override fun isFoil(stack: ItemStack): Boolean = stack.has(ModDataComponentTypes.DESTINATION)

    override fun useOn(itemUsageContext: UseOnContext): InteractionResult {
        val world = itemUsageContext.getLevel()

        if (world.isClientSide()) return InteractionResult.SUCCESS

        val player = itemUsageContext.getPlayer()

        if (ModDimensions.isPrivatePocketDimension(world) && !world.getGameRules()
                .getBoolean(ModGameRules.RIFT_SIGNATURE_WORKS_IN_PRIVATE_POCKETS)
        ) {
            player!!.displayClientMessage(
                Component.translatable("tools.signature_blocked").withStyle(ChatFormatting.BLACK), true
            )
            return InteractionResult.FAIL
        }

        // get block one block above the clicked block
        var pos = itemUsageContext.getClickedPos()
        var state = world.getBlockState(pos)
        val side = itemUsageContext.getClickedFace()


        val stack = itemUsageContext.getItemInHand()

        if (!((state.canBeReplaced() || state.getBlock() is RiftVariantProvider) && player!!.mayUseItemAt(
                pos,
                side.getOpposite(),
                stack
            ))
        ) {
            pos = pos.relative(side)
            state = world.getBlockState(pos)
        }

        pos = normalizeRiftProviderPos(world, pos)
        state = world.getBlockState(pos)

        if (!(state.canBeReplaced() || state.getBlock() is RiftVariantProvider)) {
            return InteractionResult.FAIL
        }

        var rotatedLocation: RotatedLocation? = getSource(stack)

        if (rotatedLocation == null) {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, RotatedLocation(world.dimension(), pos, player!!.getYRot(), 0f))
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stored"), true)
            world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_START, SoundSource.BLOCKS, 0.6f, 1f)
        } else {
            rotatedLocation = normalizeRiftProviderLocation(rotatedLocation)
            val source = RotatedLocation(world.dimension(), pos, player!!.getYRot(), 0f)

            val target = rotatedLocation.asTarget()
            org.dimdev.dimdoors.item.RiftSignatureItem.Companion.getOrCreateRift(
                world as net.minecraft.server.level.ServerLevel,
                pos
            )!!.ifPresent { a: Rift? -> a!!.setDestination(target) }
            org.dimdev.dimdoors.item.RiftSignatureItem.Companion.getOrCreateRift(
                rotatedLocation.world,
                rotatedLocation.blockPos
            )!!.ifPresent { a: Rift? -> a!!.setDestination(source.asTarget()) }

            val serverPlayer = player as ServerPlayer

            stack.hurtAndBreak(
                1,
                player.serverLevel(),
                serverPlayer,
                Consumer { a: Item? -> }) // TODO: calculate damage based on position?
            if (shouldclear) {
                clearSource(stack)
            }
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".created"), true)
            // null = send sound to the player too, we have to do this because this code is not run client-side
            world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_END, SoundSource.BLOCKS, 0.6f, 1f)
        }

        return InteractionResult.SUCCESS
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        list: MutableList<Component?>,
        tooltipContext: TooltipFlag
    ) {
        val transform: RotatedLocation? = getSource(itemStack)
        if (transform != null) {
            list.add(
                Component.translatable(
                    this.getDescriptionId() + ".bound.info0",
                    transform.x,
                    transform.y,
                    transform.z,
                    transform.worldId.location().toString()
                )
            )
            list.add(
                Component.translatable(
                    this.getDescriptionId() + ".bound.info1",
                    transform.worldId.location().toString()
                )
            )
        } else {
            ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".unbound.info")
        }
    }

    companion object {
        const val ID: String = "rift_signature"
        private fun normalizeRiftProviderPos(world: Level, pos: BlockPos): BlockPos {
            val state = world.getBlockState(pos)
            if (state.hasProperty<DoubleBlockHalf?>(DoorBlock.HALF) && state.getValue<DoubleBlockHalf?>(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                return pos.below()
            }
            return pos
        }

        private fun normalizeRiftProviderLocation(location: RotatedLocation): RotatedLocation {
            val world = location.world
            if (world == null) {
                return location
            }

            val normalizedPos: BlockPos = normalizeRiftProviderPos(world, location.blockPos)
            if (normalizedPos == location.blockPos) {
                return location
            }
            return RotatedLocation(location.worldId, normalizedPos, location.yaw, location.pitch)
        }

        fun setSource(itemStack: ItemStack, destination: RotatedLocation?) {
            itemStack.set<T?>(ModDataComponentTypes.DESTINATION, destination)
        }

        fun clearSource(itemStack: ItemStack) {
            itemStack.remove<T?>(ModDataComponentTypes.DESTINATION)
        }

        fun getSource(itemStack: ItemStack): RotatedLocation? {
            return itemStack.get<T?>(ModDataComponentTypes.DESTINATION)
        }

        fun getOrCreateRift(world: ServerLevel, pos: BlockPos): Optional<out Rift?>? {
            var pos = pos
            val rift: Optional<out Rift?>?

            pos = normalizeRiftProviderPos(world, pos)

            if (!LevelSpaceHelper.INSTANCE.prepareRiftCreation(world, pos)) {
                return Optional.empty<Rift?>()
            }

            val state = world.getBlockState(pos)

            if (state.getBlock() is RiftVariantProvider) rift = variantProvider.convertToRiftProvider(world, pos, state)
                .map({ obj: RiftUtils?, rift: T? -> obj!!.registerFunction(rift) })
            else if (state.canBeReplaced()) {
                world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.defaultBlockState())
                rift = world.getBlockEntity<DetachedRiftBlockEntity?>(pos, ModBlockEntityTypes.DETACHED_RIFT)
                    .map<DetachedRiftBlockEntity?>(
                        Function { obj: DetachedRiftBlockEntity? -> obj.registerFunction() })
            } else rift = Optional.empty<Rift?>()

            return rift
        }
    }
}
