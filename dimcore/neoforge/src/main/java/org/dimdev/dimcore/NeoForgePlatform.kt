package org.dimdev.dimcore

import com.mojang.brigadier.CommandDispatcher
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.EventPriority
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.server.ServerLifecycleHooks
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.ext.castOrNull
import java.nio.file.Path
import java.util.function.Consumer

class NeoForgePlatform : Platform {
    private val fuels: Object2IntMap<ItemLike?> = Object2IntLinkedOpenHashMap<ItemLike?>()
    private val strippables: MutableMap<Block?, Block?> = HashMap<Block?, Block?>()

    init {
        NeoForge.EVENT_BUS.addListener(this::getFuelBurnTime)

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, Consumer(this::modifyBlockWithTool))
    }

    private fun modifyBlockWithTool(event: BlockEvent.BlockToolModificationEvent) {
        if (event.itemAbility != ItemAbilities.AXE_STRIP || event.finalState !== event.state) return
        strippables[event.state.block]?.run { event.setFinalState(this.withPropertiesOf(event.state)) }
    }

    private fun getFuelBurnTime(event: FurnaceFuelBurnTimeEvent) {
        fuels[event.itemStack.item]?.run { event.burnTime = this }
    }

    override val server: MinecraftServer get() = ServerLifecycleHooks.getCurrentServer()!!

    override fun isModLoaded(id: String): Boolean = ModList.get().isLoaded(id)

    override val isClient: Boolean get() = FMLEnvironment.dist == Dist.CLIENT

    override fun bucketAmount(): Long = 1000

    override val configRoot: Path get() = FMLPaths.CONFIGDIR.get()

    override fun onServerStarting(consumer: (MinecraftServer) -> Unit) = NeoForge.EVENT_BUS.addListener<ServerStartingEvent> { event -> event.server.run(consumer) }

    override fun onServerStarted(consumer: (MinecraftServer) -> Unit) = NeoForge.EVENT_BUS.addListener<ServerStartedEvent> { event -> event.server.run(consumer) }

    override fun onServerStopping(consumer: (MinecraftServer) -> Unit) = NeoForge.EVENT_BUS.addListener<ServerStoppingEvent> { event -> event.server.run(consumer) }

    override fun onServerStopped(consumer: (MinecraftServer) -> Unit) = NeoForge.EVENT_BUS.addListener<ServerStoppedEvent> { event -> event.server.run(consumer) }

    override fun onPlayerJoin(consumer: (ServerPlayer) -> Unit) = NeoForge.EVENT_BUS.addListener<PlayerEvent.PlayerLoggedInEvent> { it.entity.castOrNull<ServerPlayer>()?.run(consumer) }

    override fun onPlayerQuit(consumer: (ServerPlayer) -> Unit) = NeoForge.EVENT_BUS.addListener<PlayerEvent.PlayerLoggedOutEvent> { it.entity.castOrNull<ServerPlayer>()?.run(consumer) }

    override fun onServerLevelTick(consumer: (ServerLevel) -> Unit) = NeoForge.EVENT_BUS.addListener<LevelTickEvent.Pre> { it.level.castOrNull<ServerLevel>()?.run(consumer) }

    override fun onPlayerChangeWorld(consumer: (ServerPlayer, ServerLevel, ServerLevel) -> Unit) = NeoForge.EVENT_BUS.addListener<PlayerEvent.PlayerChangedDimensionEvent> { event ->
        val player = event.entity.castOrNull<ServerPlayer>() ?: return@addListener
        val server = player.server ?: return@addListener

        val origin: ServerLevel = server.getLevel(event.from) ?: return@addListener
        val destination = server.getLevel(event.to) ?: return@addListener

        consumer.invoke(player, origin, destination)
    }

    override fun onAttackBlock(callback: Platform.AttackBlockCallback) = NeoForge.EVENT_BUS.addListener<PlayerInteractEvent.LeftClickBlock> { event ->
        val face = event.face ?: return@addListener
        if (event.action != PlayerInteractEvent.LeftClickBlock.Action.START) return@addListener

        val result = callback.attack(event.entity, event.hand, event.pos, face)
        if (result != InteractionResult.PASS) event.setCanceled(true)
    }

    override fun onUseItem(callback: Platform.UseItemCallback) = NeoForge.EVENT_BUS.addListener<PlayerInteractEvent.RightClickItem> { event ->
        val result = callback.use(event.entity, event.hand)
        if (result != InteractionResult.PASS) {
            event.setCanceled(true)
            event.cancellationResult = result
        }
    }

    override fun onUseBlock(callback: Platform.UseBlockCallback) = NeoForge.EVENT_BUS.addListener<PlayerInteractEvent.RightClickBlock>(Consumer { event ->
            val result = callback.use(event.entity, event.hand, event.hitVec)
            if (result != InteractionResult.PASS) {
                event.setCanceled(true)
                event.cancellationResult = result
            }
        })

    override fun onBeforeBlockBreak(callback: Platform.BlockBreakCallback) = NeoForge.EVENT_BUS.addListener<BlockEvent.BreakEvent> { event ->
            val level = event.level.castOrNull<Level>() ?: return@addListener

            if (callback.shouldCancel(level, event.pos, event.state, event.player)) {
                event.setCanceled(true)
            }
        }

    override fun onBeforeBlockPlace(callback: Platform.BlockPlaceCallback) = NeoForge.EVENT_BUS.addListener<BlockEvent.EntityPlaceEvent> { event ->
            val level = event.level.castOrNull<Level>() ?: return@addListener

            if (event.level is Level && callback.shouldCancel(
                    level,
                    event.pos,
                    event.placedBlock,
                    event.entity!!
                )
            ) {
                event.setCanceled(true)
            }
        }

    override fun registerCommands(consumer: (CommandDispatcher<CommandSourceStack>) -> Unit) = NeoForge.EVENT_BUS.addListener<RegisterCommandsEvent> { event -> event.dispatcher.run(consumer) }

    override fun <T : CustomPacketPayload> sendPacket(player: ServerPlayer, packet: T) = PacketDistributor.sendToPlayer(player, packet)

    override fun <T : CustomPacketPayload> sendPacket(packet: T) = PacketDistributor.sendToServer(packet)

    override fun canSend(player: ServerPlayer, type: CustomPacketPayload.Type<*>): Boolean = player.connection.hasChannel(type)

    override fun registerFuel(item: ItemLike, amount: Int) { fuels.put(item, amount) }

    override fun registerStrippable(source: Block, target: Block) { strippables[source] = target }

    override fun registerFlammable(block: Block, encouragement: Int, flammability: Int) = Blocks.FIRE.cast<FireBlock>().setFlammable(block, encouragement, flammability)
}
