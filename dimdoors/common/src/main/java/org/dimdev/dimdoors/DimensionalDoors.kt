package org.dimdev.dimdoors

import com.mojang.logging.LogUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.EntityAttributeProvider
import org.dimdev.dimcore.api.EntityAttributeRegister
import org.dimdev.dimcore.api.Hooks
import org.dimdev.dimcore.api.PackProvider
import org.dimdev.dimcore.api.PackRegister
import org.dimdev.dimcore.api.PacketProvider
import org.dimdev.dimcore.api.RegistrationHooks
import org.dimdev.dimcore.api.ServerReloadListenerProvider
import org.dimdev.dimcore.api.ServerReloadListenerRegister
import org.dimdev.dimcore.api.ModCommon
import org.dimdev.dimcore.api.PacketRegister
import org.dimdev.dimcore.api.event.PlayerTeleportEvents
import org.dimdev.dimdoors.ModRegistries.register
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.api.util.LocationCondition
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.client.ModRecipeBookTypes
import org.dimdev.dimdoors.command.ModCommands
import org.dimdev.dimdoors.criteria.ModCriteria
import org.dimdev.dimdoors.enchantment.ModEnchantmentEffects
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.entity.ModEntityTypes.MASK
import org.dimdev.dimdoors.entity.ModEntityTypes.MONOLITH
import org.dimdev.dimdoors.entity.MonolithEntity
import org.dimdev.dimdoors.entity.stat.ModStats
import org.dimdev.dimdoors.fluid.ModFluids
import org.dimdev.dimdoors.item.ModArmorMaterials
import org.dimdev.dimdoors.item.ModDataComponentTypes
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar
import org.dimdev.dimdoors.item.door.data.condition.Conditions
import org.dimdev.dimdoors.item.loot.ModItemLootConditions
import org.dimdev.dimdoors.listener.AttackBlockCallbackListener
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener
import org.dimdev.dimdoors.listener.pocket.*
import org.dimdev.dimdoors.network.ServerPacketHandler
import org.dimdev.dimdoors.network.client.ClientPacketListener
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket
import org.dimdev.dimdoors.network.packet.s2c.*
import org.dimdev.dimdoors.particle.ModParticleTypes
import org.dimdev.dimdoors.pockets.PocketLoader
import org.dimdev.dimdoors.pockets.generator.PocketGeneratorType
import org.dimdev.dimdoors.pockets.generator.PocketGenerators
import org.dimdev.dimdoors.pockets.modifier.Modifiers
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import org.dimdev.dimdoors.recipe.ModRecipeSerializers
import org.dimdev.dimdoors.recipe.ModRecipeTypes
import org.dimdev.dimdoors.rift.registry.LegacyDimensionalRegistryMigrator
import org.dimdev.dimdoors.rift.registry.RegistryVertices
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.rift.registry.SubsystemTypes
import org.dimdev.dimdoors.rift.targets.Targets
import org.dimdev.dimdoors.rift.targets.VirtualTargets
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.world.ModBiomes
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.ModStructureProccessors
import org.dimdev.dimdoors.world.carvers.ModCarvers
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.conditions.DecayConditionType
import org.dimdev.dimdoors.world.decay.pattern.DecayPatternType
import org.dimdev.dimdoors.world.decay.results.DecayResultType
import org.dimdev.dimdoors.world.DataValues
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket
import org.dimdev.dimdoors.world.pocket.type.Pockets
import org.dimdev.dimdoors.world.pocket.type.Pocket
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons
import org.dimdev.dimdoors.world.pocket.type.addon.PortalColorProvider
import org.dimdev.dimdoors.world.pocket.type.addon.PreventBlockModificationAddon
import org.slf4j.Logger
import java.util.*

class DimensionalDoors : ModCommon<IDimensionalDoorsSided<out IDimensionalDoorsSided<*>>>, EntityAttributeProvider, PacketProvider, RegistrationHooks, PackProvider, ServerReloadListenerProvider {


    override fun init(sided: IDimensionalDoorsSided<*>) {
        Companion.sided = sided

        reloadConfig()

        registerRegistries()

        ModRecipeBookTypes.init()

        ModDataComponentTypes.register()
        ModEnchantmentEffects.init()
        ModItemLootConditions.init()

        ModCarvers.register()
        ModRecipeTypes.init()
        ModRecipeSerializers.register()
        ModScreenHandlerTypes.init()
        ModSoundEvents.init()
        ModFluids.register()
        ModEntityTypes.register()
        ModItems.register()
        ModArmorMaterials.init()
        ModBlocks.init()
        ModBlockEntityTypes.init()
        ModCarvers.register()
        ModBiomes.register()
        ModStats.register()
        ModParticleTypes.init()
        ModCriteria.init()
        ModStructureProccessors.init()

        DataValues.register()


        ModGameRules.init()

        ModCommands.register()
        ModDimensions.register()
        sided.checkCompat()
        ModItems.DIMENSIONAL_DOORS.addAllAfter({ ModBlocks.REALITY_SPONGE.value() }) { DimensionalDoorItemRegistrar.autogeneratedItems }

        //        ModRecipeBookTypes.init();
        platform.onServerStarting(Decay.DecayLoader::populate)

        PlayerTeleportEvents.BEFORE.register { player, level, pos ->
            val pocket = ServerPacketHandler.PlayerSyncData.getPocket(level, BlockPos.containing(pos))
            syncColors(player, pocket)
            syncPocket(player, pocket)
        }

        platform.onPlayerJoin { player ->
            val pocket = ServerPacketHandler.PlayerSyncData.getPocket(
                player.serverLevel(),
                BlockPos.containing(player.position())
            )

            syncColors(player, pocket)
            syncPocket(player, pocket)
        }

        platform.onServerStopping {
            Decay.clearQueue()
            ServerPacketHandler.clear()
        }

        registerListeners()
        //        SchemFixer.run();
    }

    private fun syncColors(player: ServerPlayer, pocket: Pocket<*, *>?) {
        var colors: IntArray?

        if (pocket != null) {
            colors = pocket.streamAddon()
                .filter { obj: PocketAddon? -> PortalColorProvider::class.java.isInstance(obj) }
                .filterIsInstance<PortalColorProvider>().firstNotNullOfOrNull { obj -> obj.colors }
                ?: PortalColors.base()
        } else {
            val level = player.serverLevel()
            colors = PortalColors.levels(level.dimension())
            if (colors == null) colors = PortalColors.base()
        }

        platform.sendPacket(player, PortalColorsS2CPacket(colors))
    }

    private fun syncPocket(player: ServerPlayer, pocket: Pocket<*, *>?) = if (pocket != null) {
        ServerPacketHandler.syncPocketAddonsIfNeeded(player, pocket)
    } else {
        ServerPacketHandler.clearPocketIfNeeded(player)
    }

    override val modId: String get() = "dimdoors"

    override fun registerEntityAttributes(register: EntityAttributeRegister) {
        register.register(MONOLITH, MonolithEntity::createMobAttributes)
        register.register(MASK, MonolithEntity::createMobAttributes)
    }

    override fun registerPacks(register: PackRegister) {
        register.addPack(PackType.SERVER_DATA, "default", "Default", true)
        register.addPack(PackType.SERVER_DATA, "classic", "Classic", true)
    }

    override fun registerServerReloadListeners(register: ServerReloadListenerRegister) {
        register.register("pocket_loader", PocketLoader::reload)
        register.register("decay_loader", true, Decay.DecayLoader::reload)
        register.register("portal_colors") { _, manager -> PortalColors.load(manager) }

        //        sided.registerServerLoader("door_data_loader", DoorRiftDataLoader::reload);
    }

    override fun registrationHooks(hooks: Hooks) {
        hooks.onEachEntry(Registries.BLOCK, dimensionalDoorBlockRegistrar::handleEntry)
        hooks.onEachEntry(Registries.ITEM, dimensionalDoorItemRegistrar::handleEntry)
    }

    override fun registerPackets(packetRegister: PacketRegister) {
        packetRegister.registerClientPacket(PlayerInventorySlotUpdateS2CPacket.TYPE, PlayerInventorySlotUpdateS2CPacket.STREAM_CODEC, ClientPacketListener::onPlayerInventorySlotUpdate)
        packetRegister.registerClientPacket(SyncPocketAddonsS2CPacket.TYPE, SyncPocketAddonsS2CPacket.STREAM_CODEC, ClientPacketListener::onSyncPocketAddons)
        packetRegister.registerClientPacket(MonolithAggroParticlesPacket.TYPE, MonolithAggroParticlesPacket.STREAM_CODEC, ClientPacketListener::onMonolithAggroParticles)
        packetRegister.registerClientPacket(MonolithTeleportParticlesPacket.TYPE, MonolithTeleportParticlesPacket.STREAM_CODEC, ClientPacketListener::onMonolithTeleportParticles)
        packetRegister.registerClientPacket(RenderBreakBlockS2CPacket.TYPE, RenderBreakBlockS2CPacket.STREAM_CODEC, ClientPacketListener::onRenderBreakBlock)

        packetRegister.registerServerPacket<HitBlockWithItemC2SPacket>(HitBlockWithItemC2SPacket.TYPE, HitBlockWithItemC2SPacket.STREAM_CODEC, ) { packet, player -> ServerPacketHandler.onAttackBlock(player, packet) }

        packetRegister.registerClientPacket(PortalColorsS2CPacket.TYPE, PortalColorsS2CPacket.STREAM_CODEC, ClientPacketListener::onPortalColors)
        packetRegister.registerClientPacket(ClearPocketS2CPacket.TYPE, ClearPocketS2CPacket.STREAM_CODEC, ClientPacketListener::onClearPocket)

    }

    companion object {
        @JvmField
        val INSTANCE: DimensionalDoors = DimensionalDoors()

        const val MOD_ID: String = "dimdoors"
        @JvmField
        val LOGGER: Logger = LogUtils.getLogger()

        private lateinit var dimensionalDoorItemRegistrar: DimensionalDoorItemRegistrar
        private lateinit var dimensionalDoorBlockRegistrar: DimensionalDoorBlockRegistrar

        private lateinit var sided: IDimensionalDoorsSided<*>

        @JvmStatic fun id(id: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, id)
        @JvmStatic fun String.id(): ResourceLocation = id(this)

        @JvmStatic
        val server: MinecraftServer get() = platform.server

        fun getWorld(world: ResourceKey<Level>): ServerLevel? = platform.server.getLevel(world)

        @JvmStatic
        lateinit var config: ModConfig
            private set

        @JvmStatic
        fun saveConfig() = ModConfig.type.save(sided.configPath().resolve("-config.json"), config)

        fun reloadConfig() {
            config = ModConfig.type.load(sided.configPath().resolve("-config.json"))
        }



        fun registerRegistries() {
            Targets.registerDefaultTargets()
            VirtualTargets.register()
            VirtualPockets.register()
            RegistryVertices.register()
            Modifiers.register()
            PocketGenerators.register()
            Pockets.register()
            PocketAddons.register()
            SubsystemTypes.register()
            Conditions.register()
            DecayConditionType.register()
            DecayResultType.register()
            DecayPatternType.register()
            LocationCondition.LocationConditionType.register()
            register()
        }

        private fun registerListeners() {
//        sided.onPlayerQuit(player -> PocketCommand.logSetting.remove(player.getUUID()));

            platform.onServerStarted { server ->
                LegacyDimensionalRegistryMigrator.migrateIfNeeded(server)
                SubSystem.initialize(server)
            }

            platform.onAttackBlock(AttackBlockCallbackListener())
            platform.onAttackBlock(PocketAttackBlockCallbackListener())

            platform.onBeforeBlockBreak(PlayerBlockBreakEventBeforeListener())



            platform.onUseItem(UseItemCallbackListener())
            UseItemOnBlockCallback.EVENT.register(UseItemOnBlockCallbackListener())
            platform.onUseBlock(UseBlockCallbackListener())
            platform.onBeforeBlockPlace { level: Level, pos: BlockPos, _, placer: Entity -> shouldCancelBlockModification(level, pos, placer)
            }

            UseItemOnBlockCallback.EVENT.register(UseDoorItemOnBlockCallbackListener())

            platform.onServerLevelTick(Decay::tick)
        }

        private fun shouldCancelBlockModification(level: Level, pos: BlockPos, actor: Entity?): Boolean = !(actor is Player && actor.isCreative) && PocketListenerUtil.getAddon<PreventBlockModificationAddon>(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos) != null

        @JvmStatic
        fun getDimensionalDoorItemRegistrar(): DimensionalDoorItemRegistrar {
            return dimensionalDoorItemRegistrar
        }

        @JvmStatic
        fun getDimensionalDoorBlockRegistrar(): DimensionalDoorBlockRegistrar {
            return dimensionalDoorBlockRegistrar
        }

        @JvmStatic
        fun afterBlockBreak(world: Level, player: Player, pos: BlockPos, state: BlockState, blockEntity: BlockEntity?) {
            var pos = pos
            if (player.isCreative() && !config.doorsConfig.placeRiftsInCreativeMode) {
                return
            }

            if (blockEntity is Rift) {
                if (state.getBlock() is DoorBlock && state.getValue<DoubleBlockHalf?>(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                    pos = pos.below()
                }

                var detachedState = ModBlocks.DETACHED_RIFT.value().defaultBlockState()
                if (state.hasProperty(WaterLoggableDoorBlock.WATERLOGGED)) detachedState =
                    detachedState.setValue(
                        WaterLoggableDoorBlock.WATERLOGGED, state.getValue(WaterLoggableDoorBlock.WATERLOGGED)
                    )

                world.setBlockAndUpdate(pos, detachedState)
                world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).ifPresent { rift ->
                    rift.data = blockEntity.data
                }
            }
        }

        @JvmStatic
        fun getSided(): IDimensionalDoorsSided<*> {
            return sided
        }
    }
}
