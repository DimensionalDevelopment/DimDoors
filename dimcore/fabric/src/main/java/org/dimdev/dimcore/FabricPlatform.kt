package org.dimdev.dimcore

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.*
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.Platform
import java.nio.file.Path
import java.util.function.Consumer

class FabricPlatform : Platform {
    override lateinit var server: MinecraftServer

    init {
        SERVER_STARTING.register { server -> this.server = server }
    }

    override fun isModLoaded(id: String): Boolean = FabricLoader.getInstance().isModLoaded(id)

    override val isClient: Boolean = FabricLoader.getInstance().environmentType == EnvType.CLIENT

    override fun bucketAmount(): Long = 81000

    override val configRoot: Path get() = FabricLoader.getInstance().configDir

    override fun onServerStarting(consumer: (MinecraftServer) -> Unit) = SERVER_STARTING.register { consumer(it) }

    override fun onServerStarted(consumer: (MinecraftServer) -> Unit) = SERVER_STARTED.register { consumer(it) }

    override fun onServerStopping(consumer: (MinecraftServer) -> Unit) = SERVER_STOPPING.register { consumer(it) }

    override fun onServerStopped(consumer: (MinecraftServer) -> Unit) = SERVER_STOPPED.register { consumer(it) }

    override fun onPlayerJoin(consumer: (ServerPlayer) -> Unit) = ServerPlayConnectionEvents.JOIN.register { handler, _, _ -> consumer.invoke(handler.player) }

    override fun onPlayerQuit(consumer: (ServerPlayer) -> Unit) = ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> consumer.invoke(handler.player) }

    override fun onServerLevelTick(consumer: (ServerLevel) -> Unit) = ServerTickEvents.START_WORLD_TICK.register(ServerTickEvents.StartWorldTick { consumer.invoke(it) })

    override fun onPlayerChangeWorld(consumer: (ServerPlayer, ServerLevel, ServerLevel) -> Unit) = ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register { player, before, after -> consumer.invoke(player, before, after) }

    override fun onAttackBlock(callback: Platform.AttackBlockCallback) = AttackBlockCallback.EVENT.register { player, _, hand, pos, direction -> callback.attack(player, hand, pos, direction) }

    override fun onUseItem(callback: Platform.UseItemCallback) = UseItemCallback.EVENT.register { player, _, hand -> InteractionResultHolder(callback.use(player, hand), player.getItemInHand(hand)) }

    override fun onUseBlock(callback: Platform.UseBlockCallback) = UseBlockCallback.EVENT.register { player, _, hand, hitResult -> callback.use(player, hand, hitResult) }

    override fun onBeforeBlockBreak(callback: Platform.BlockBreakCallback) = PlayerBlockBreakEvents.BEFORE.register { level, player, pos, state, _ -> !callback.shouldCancel(level, pos, state, player) }

    override fun onBeforeBlockPlace(callback: Platform.BlockPlaceCallback) {
        UseBlockCallback.EVENT.register { player, level, hand, hitResult ->
            if (player.getItemInHand(hand).item !is BlockItem) return@register InteractionResult.PASS
            val pos = hitResult.blockPos
            if (callback.shouldCancel(level, hitResult.blockPos, level.getBlockState(pos), player)) InteractionResult.FAIL else InteractionResult.PASS
        }
    }

    override fun registerCommands(consumer: (CommandDispatcher<CommandSourceStack>) -> Unit) = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> consumer.invoke(dispatcher) }

    override fun <T : CustomPacketPayload> sendPacket(player: ServerPlayer, packet: T) = ServerPlayNetworking.send(player, packet)

    override fun <T : CustomPacketPayload> sendPacket(packet: T) = ClientPlayNetworking.send(packet)

    override fun canSend(player: ServerPlayer, type: CustomPacketPayload.Type<*>): Boolean = ServerPlayNetworking.canSend(player, type)

    override fun registerFuel(item: ItemLike, amount: Int) = FuelRegistry.INSTANCE.add(item, amount)

    override fun registerStrippable(source: Block, target: Block) = StrippableBlockRegistry.register(source, target)

    override fun registerFlammable(block: Block, encouragement: Int, flammability: Int) = FlammableBlockRegistry.getDefaultInstance().add(block, encouragement, flammability)
}
