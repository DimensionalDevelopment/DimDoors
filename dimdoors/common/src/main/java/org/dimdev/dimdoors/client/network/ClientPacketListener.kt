package org.dimdev.dimdoors.client.network

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.BuilderType
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.client.ModShaders
import org.dimdev.dimdoors.client.particle.MonolithParticle
import org.dimdev.dimdoors.entity.monolith.MonolithEntity
import org.dimdev.dimdoors.item.MaskWearerEffects
import org.dimdev.dimdoors.network.s2c.*
import org.dimdev.dimdoors.pockets.addon.PocketAddon
import org.dimdev.dimdoors.pockets.addon.PocketAddonType
import org.dimdev.dimdoors.pockets.addon.PocketAddons

object ClientPacketListener {
    private val LOGGER: Logger = LogManager.getLogger()

    private val clientRandom: RandomSource = RandomSource.create()

    var pocketWorld: ResourceKey<Level>? = null
        private set
    var addons = mutableMapOf<BuilderType<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>>, PocketAddon>()
        private set

    fun <T : CustomPacketPayload> sendPacket(packet: T) = platform.sendPacket(packet)

    fun <T : CustomPacketPayload> tryToSendPacket(packet: T): Boolean {
        try {
            sendPacket(packet)
            return true
        } catch (e: Exception) {
            LOGGER.error(e)
            return false
        }
    }

    fun clearPocketAddons() {
        pocketWorld = null
        addons = mutableMapOf()
    }

    fun onPlayerInventorySlotUpdate(packet: PlayerInventorySlotUpdateS2CPacket) {
        Minecraft.getInstance().execute {
            Minecraft.getInstance().player?.getInventory()?.setItem(packet.slot, packet.stack)
        }
    }

    fun onSyncPocketAddons(packet: SyncPocketAddonsS2CPacket) {
        val hadMusic = hasMusicAddon()

        pocketWorld = packet.world
        addons = packet.addons.associateBy { it.type }.toMutableMap()

        if (hadMusic || hasMusicAddon()) stopMusic()
    }

    private fun hasMusicAddon(): Boolean = addons.containsKey(PocketAddons.MUSIC_ADDON)

    private fun stopMusic() = Minecraft.getInstance().execute { Minecraft.getInstance().musicManager.stopPlaying() }

    fun onMonolithAggroParticles(packet: MonolithAggroParticlesPacket) = Minecraft.getInstance().execute { spawnParticles(packet.aggro) }

    fun spawnParticles(aggro: Int) {
        val player = Minecraft.getInstance().player ?: return

        if (aggro < 120) {
            return
        }
        val count = 10 * aggro / MonolithEntity.MAX_AGGRO
        for (i in 1..<count) {
            player.level().addParticle(
                ParticleTypes.PORTAL, player.x + (clientRandom.nextDouble() - 0.5) * 3.0,
                player.y + clientRandom.nextDouble() * player.bbHeight - 0.75,
                player.z + (clientRandom.nextDouble() - 0.5) * player.bbWidth,
                (clientRandom.nextDouble() - 0.5) * 2.0, -clientRandom.nextDouble(),
                (clientRandom.nextDouble() - 0.5) * 2.0
            )
        }
    }

    fun onMonolithTeleportParticles(packet: MonolithTeleportParticlesPacket) {
        Minecraft.getInstance().apply {
            execute {
                val level = level ?: return@execute
                val pos = player?.position() ?: return@execute

                particleEngine.add(
                    MonolithParticle(level, pos)
                )
            }
        }
    }

    fun <T : PocketAddon> getAddonClient(type: PocketAddonType<T, *>, world: Level, pos: BlockPos): T? {
        if (world.dimension() != pocketWorld) return null
        return addons[type]?.cast()
    }

    fun onPortalColors(packet: PortalColorsS2CPacket) {
        ModShaders.setPortalColors(packet.colors)
    }

    fun onClearPocket(packet: ClearPocketS2CPacket) {
        val hadMusic = hasMusicAddon()

        clearPocketAddons()

        if (hadMusic) stopMusic()
    }

    fun onMaskCatch(packet: MaskCatchAnimS2CPacket) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(MaskWearerEffects.createMaskStack(packet.maskType))
    }
}
