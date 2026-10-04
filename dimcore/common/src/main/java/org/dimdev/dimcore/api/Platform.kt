package org.dimdev.dimcore.api

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import java.nio.file.Path

interface Platform {
    val server: MinecraftServer
    fun isModLoaded(id: String): Boolean
    val isClient: Boolean
    fun bucketAmount(): Long
    val configRoot: Path

    fun onServerStarting(consumer: (MinecraftServer) -> Unit)
    fun onServerStarted(consumer: (MinecraftServer) -> Unit)
    fun onServerStopping(consumer: (MinecraftServer) -> Unit)
    fun onServerStopped(consumer: (MinecraftServer) -> Unit)
    fun onPlayerJoin(consumer: (ServerPlayer) -> Unit)
    fun onPlayerQuit(consumer: (ServerPlayer) -> Unit)
    fun onServerLevelTick(consumer: (ServerLevel) -> Unit)
    fun onPlayerChangeWorld(consumer: (ServerPlayer, ServerLevel, ServerLevel) -> Unit)
    fun onAttackBlock(callback: AttackBlockCallback)
    fun onUseItem(callback: UseItemCallback)
    fun onUseBlock(callback: UseBlockCallback)
    fun onBeforeBlockBreak(callback: BlockBreakCallback)
    fun onBeforeBlockPlace(callback: BlockPlaceCallback)
    fun registerCommands(consumer: (CommandDispatcher<CommandSourceStack>) -> Unit)
    fun <A : ArgumentType<*>> registerArgumentType(id: ResourceLocation, clazz: Class<A>, supplier: () -> A)
    fun <T : CustomPacketPayload> sendPacket(player: ServerPlayer, packet: T)
    fun <T : CustomPacketPayload> sendPacket(packet: T)

    fun canSend(player: ServerPlayer, type: CustomPacketPayload.Type<*>): Boolean

    fun registerFuel(item: ItemLike, amount: Int)
    fun registerStrippable(source: Block, target: Block)
    fun registerFlammable(block: Block, encouragement: Int, flammability: Int)

    fun interface AttackBlockCallback {
        fun attack(player: Player, hand: InteractionHand, pos: BlockPos, direction: Direction): InteractionResult
    }

    fun interface UseItemCallback {
        fun use(player: Player, hand: InteractionHand): InteractionResult
    }

    fun interface UseBlockCallback {
        fun use(player: Player, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult
    }

    fun interface BlockBreakCallback {
        fun shouldCancel(level: Level, pos: BlockPos, state: BlockState, player: Player): Boolean
    }

    fun interface BlockPlaceCallback {
        fun shouldCancel(level: Level, pos: BlockPos, state: BlockState, placer: Entity): Boolean
    }
}
