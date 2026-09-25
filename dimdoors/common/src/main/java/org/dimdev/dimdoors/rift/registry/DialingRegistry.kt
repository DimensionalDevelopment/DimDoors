package org.dimdev.dimdoors.rift.registry

import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec.unboundedMap
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.util.CodecUtils.unboundedMap
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.DialingPocket
import org.dimdev.dimdoors.world.pocket.PocketInfo
import java.util.*
import java.util.function.Function

class DialingRegistry(
    locations: MutableMap<UUID, PlayerRiftConnection> = mutableMapOf(),
    private var dialingPockets: HashBiMap<DialingAddress, PocketInfo> = HashBiMap.create<DialingAddress, PocketInfo>(),
    private var playertoAddress: MutableMap<UUID, DialingAddress> = HashMap<UUID, DialingAddress>()
) : PlayerTrackingSubSystem<DialingAddress?, DialingPocket, DialingRegistry>(locations) {

    override fun getKeyFromPlayer(playerUUID: UUID?): DialingAddress? {
        return playertoAddress[playerUUID]
    }

    override fun invalidKeyErrorMessage(): String = "Cannot resolve dialing entrance for {} because no active dialing address is tracked."

    public override fun invalidPocketErrorMessage(): String = "Cannot resolve dialing entrance for {} at {} because no dialing pocket is tracked."

    public override fun getPocketFromKey(uuid: DialingAddress?): DialingPocket? = this.dialingPockets[uuid]?.let { PocketRegistry.getInstance().getPocket<DialingPocket?>(it, DialingPocket::class.java) }

    public override fun setNewPocket(uuid: UUID?, key: DialingAddress?, pocket: DialingPocket?) {
        setDialingPocketAddress(key, pocket)
        setPlayerAddress(uuid, key)
    }

    public override fun isCorrectDimensionForPocket(world: ServerLevel): Boolean = ModDimensions.isPocketDimension(world)

    public override fun setCurrentKey(uuid: UUID?, key: DialingAddress?) = setPlayerAddress(uuid, key)

    fun setPlayerAddress(uuid: UUID?, address: DialingAddress?) {
        Objects.requireNonNull<UUID>(uuid, "uuid")
        Objects.requireNonNull<DialingAddress>(address, "address")

        val previous = this.playertoAddress.put(uuid!!, address!!)
        if (address != previous) {
            this.setDirty()
        }
    }

    public override fun type(): Type<DialingRegistry> {
        return SubsystemTypes.DIALING
    }

    fun setDialingPocketAddress(address: DialingAddress?, pocket: DialingPocket?) {
        Objects.requireNonNull<DialingAddress?>(address, "address")
        Objects.requireNonNull<DialingPocket?>(pocket, "pocket")

        val info = PocketInfo(pocket!!.getWorld(), pocket.getId())
        val pocketAddress = pocket.address
        check(address == pocketAddress) { "Dialing pocket ${info.world.location()}:${info.id} has address $pocketAddress, cannot assign registry address $address" }

        val existingAddress = this.dialingPockets.inverse().get(info)
        check(!(existingAddress != null && existingAddress != address)) { "Dialing pocket ${info.world.location()}:${info.id} is already assigned to $existingAddress, cannot assign to $address" }

        val previous = this.dialingPockets.put(address, info)

        logger.info(
            "Tracking dialing pocket address {} -> {}:{} (previous={}, pocketAddress={})",
            address, info.world.location(), info.id, previous, pocketAddress
        )

        if (info != previous) {
            this.setDirty()
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> commonFields(instance)
                    .and(unboundedMap(
                            DialingAddress.STRING_CODEC,
                            PocketInfo.CODEC,
                            HashBiMap<DialingAddress, PocketInfo>::create).fieldOf(
                            "dialing_pockets"
                        ).forGetter(DialingRegistry::dialingPockets)
                    ).and(
                        unboundedMap<UUID?, DialingAddress?>(
                            UUIDUtil.STRING_CODEC,
                            DialingAddress.CODEC
                        ).fieldOf("player_to_address").forGetter(DialingRegistry::playertoAddress)
                    ).apply(instance, ::DialingRegistry)
            }

        @JvmStatic
        val instance: DialingRegistry get() = getInstance(SubsystemTypes.DIALING)!!
    }
}
