package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.util.SimpleEvent
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.PocketDirectory
import org.dimdev.dimdoors.world.pocket.PocketInfo
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

class PocketRegistry(
    val directories: MutableMap<ResourceKey<Level>, PocketDirectory> = mutableMapOf(),
) : SubSystem<PocketRegistry>() {

    fun forEachPocketDirectory(consumer: (ResourceKey<Level>, PocketDirectory) -> Unit) {
        directories.forEach(consumer)
    }

    fun peekPocketDirectory(key: ResourceKey<Level>): PocketDirectory? {
        if (!ModDimensions.isPocketDimension(key)) {
            return null
        }

        return directories[key]
    }

    fun getOrCreate(key: ResourceKey<Level>): PocketDirectory {
        return directories.computeIfAbsent(key) { key -> this.createDirectory(key) }
    }

    private fun createDirectory(key: ResourceKey<Level>): PocketDirectory {
        val directory = PocketDirectory()
        this.setDirty()
        return directory
    }

    fun getPocketDirectory(key: ResourceKey<Level>): PocketDirectory {
        if (!ModDimensions.isPocketDimension(key)) {
            throw UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!")
        }

        return instance.getOrCreate(key)
    }

    fun createPocket(key: ResourceKey<Level>, builder: Pocket.PocketBuilder<*, *>): Pocket<*, *>? {
        return getPocketDirectory(key).newPocket(key, builder)
    }

    override fun type(): Type<PocketRegistry> {
        return SubsystemTypes.POCKET
    }

    fun <T : Pocket<*, *>> getPocket(info: PocketInfo, clazz: Class<T>): T? =
        getPocketDirectory(info.world).getPocket<T>(info.id, clazz)

    fun getPocketEntrances(pocket: Pocket<*, *>): MutableSet<Location> {
        Objects.requireNonNull(pocket, "pocket")
        return this.getPocketEntrances(PocketInfo(pocket.world, pocket.id))
    }

    fun getPocketEntrances(info: PocketInfo): MutableSet<Location> {
        return getPocketEntrances(info.uuid)
    }

    fun getPocketEntrances(info: UUID): MutableSet<Location> = RiftGraph.getInstance().targets(info).mapNotNull { RiftRegistry.instance.locationOf(it) }.toMutableSet()

    fun getPocketEntrance(pocket: Pocket<*, *>?): Location? {
        requireNotNull(pocket) { "pocket" }
        return this.getPocketEntrance(PocketInfo(pocket.world, pocket.id))
    }

    fun getPocketEntrance(pocketId: UUID): Location? = this.getPocketEntrances(pocketId).firstOrNull()
    fun getPocketEntrance(info: PocketInfo): Location? = this.getPocketEntrances(info).firstOrNull()

    fun addPocketEntrance(pocket: Pocket<*, *>?, location: Location?) {
        requireNotNull(pocket) { "pocket" }
        requireNotNull(location) { "location" }

        val info = PocketInfo(pocket.world, pocket.id)
        val pointer = info.uuid

        PocketEvents.ADDED_POCKET_ENTRANCE.invoker().addPocketEntrance(pointer, location)
    }

    object PocketEvents {
        val ADDED_POCKET_ENTRANCE : SimpleEvent<PocketEntranceAdd> = SimpleEvent.of  { callbacks -> { id, location -> callbacks.forEach { it.addPocketEntrance(id, location) } } }

        fun interface PocketEntranceAdd { fun addPocketEntrance(id: UUID, location: Location) }
    }

    fun getPocketAt(provider: LocationProvider?): Pocket<*, *>? = provider?.providedLocation?.let { location -> location.worldId?.let { directories[it] }?.getPocketAt(location.blockPos) }

    fun <P : Pocket<*, *>> getPocketAt(location: LocationProvider?, pocketClass: Class<P>): P? = getPocketAt(location)?.cast(pocketClass)

    companion object {
        @JvmField
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Level.RESOURCE_KEY_CODEC.unboundedMap(PocketDirectory.CODEC).fieldOf("directories").forGetter(PocketRegistry::directories)
                ).apply(instance, ::PocketRegistry)
            }

        @JvmStatic
        val instance: PocketRegistry get() = getInstance(SubsystemTypes.POCKET)!!
    }
}
