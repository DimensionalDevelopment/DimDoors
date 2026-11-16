package org.dimdev.dimdoors.subsystem.player

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.pockets.PocketInfo
import org.dimdev.dimdoors.pockets.type.HubPocket
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import org.dimdev.dimdoors.worldgen.ModDimensions
import java.util.*

class HubRegistry(locations: MutableMap<UUID, UUID> = mutableMapOf(), private var pocket: PocketInfo? = null) : PlayerTrackingSubSystem<HubRegistry.HubKey, HubPocket, HubRegistry>(locations) {
    override fun getKeyFromPlayer(playerUUID: UUID?) = HubKey

    override fun invalidKeyErrorMessage(): String = throw UnsupportedOperationException("Key for HubRegistry is a guaranteed singleton.")
    override fun invalidPocketErrorMessage(): String = "Cannot resolve hub entrance for {} at {} because no hub pocket is tracked."

    override fun getPocketFromKey(uuid: HubKey?): HubPocket? = pocket?.let { PocketRegistry.instance.getPocket(it, HubPocket::class.java) }
    override fun setNewPocket(uuid: UUID?, key: HubKey?, pocket: HubPocket?) {
        val info = requireNotNull(pocket).let { PocketInfo(it.world, it.id) }
        if (this.pocket != info) {
            this.pocket = info
            setDirty()
        }
    }
    override fun isCorrectDimensionForPocket(world: ServerLevel) = ModDimensions.isPrivatePocketDimension(world)
    override fun setCurrentKey(uuid: UUID?, key: HubKey?) {}

    override fun type(): Type<HubRegistry> = SubsystemTypes.HUB

    object HubKey

    companion object {
        @JvmField
        val CODEC: MapCodec<HubRegistry> = RecordCodecBuilder.mapCodec { instance -> commonFields(instance)
            .and(PocketInfo.CODEC.optionalFieldOf("pocket").forGetter { Optional.ofNullable(it.pocket) })
            .apply(instance) { locations, pocket -> HubRegistry(locations, pocket.orElse(null)) }
        }

        @JvmStatic
        val instance: HubRegistry get() = getInstance(SubsystemTypes.HUB)!!
    }
}