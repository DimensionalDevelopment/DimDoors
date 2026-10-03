package org.dimdev.dimdoors.network.client

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.BuilderType
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.client.ModShaders
import org.dimdev.dimdoors.entity.MonolithEntity
import org.dimdev.dimdoors.network.packet.s2c.*
import org.dimdev.dimdoors.particle.client.MonolithParticle
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddonType
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons
import java.lang.Long
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.apply

object ClientPacketListener {
    private val LOGGER: Logger = LogManager.getLogger()

    private val clientRandom: RandomSource = RandomSource.create()

    var pocketWorld: ResourceKey<Level>? = null
        private set
    var area: BoundingBox? = null
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
        area = null
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
        area = packet.box
        addons = packet.addons.associateBy { it.type }.toMutableMap()

        if (hadMusic || hasMusicAddon()) stopMusic()
    }

    private fun hasMusicAddon(): Boolean = addons.containsKey(PocketAddons.MUSIC_ADDON)

    private fun stopMusic() = Minecraft.getInstance().soundManager.stop(null, SoundSource.MUSIC)

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
                particleEngine.add(
                    MonolithParticle(
                        level!!,
                        player!!.x,
                        player!!.y,
                        player!!.z
                    )
                )
            }
        }
    }

    fun onRenderBreakBlock(packet: RenderBreakBlockS2CPacket) {
        val client = Minecraft.getInstance()
        client.execute {
            if (client.level == null) {
                return@execute
            }
            client.levelRenderer.destroyBlockProgress(Long.hashCode(packet.pos.asLong()), packet.pos, packet.stage)
        }
    }

    fun <T : PocketAddon> getAddonClient(type: PocketAddonType<T>, world: Level, pos: BlockPos): T? {
        if (world.dimension() != pocketWorld) return null

        if (!area!!.isInside(pos.x, pos.y, pos.z)) return null


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
}
