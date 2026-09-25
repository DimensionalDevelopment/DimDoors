package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec.unboundedMap
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.cast
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.util.CodecUtils.unboundedMap
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.PocketDirectory
import org.dimdev.dimdoors.world.pocket.PocketInfo
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*
import java.util.Objects.requireNonNull
import java.util.function.*
import java.util.function.Function
import java.util.stream.Collectors

class PocketRegistry(
    val directories: MutableMap<ResourceKey<Level>, PocketDirectory> = mutableMapOf(),
    val pocketEntrancePointers: MutableMap<PocketInfo, PocketEntrancePointer> = mutableMapOf()
) : SubSystem<PocketRegistry>(), VertexProvider {

    fun forEachPocketDirectory(consumer: (ResourceKey<Level>, PocketDirectory) -> Unit) {
        directories.forEach(consumer)
    }

    fun peekPocketDirectory(key: ResourceKey<Level>): PocketDirectory? {
        if (!ModDimensions.isPocketDimension(key)) {
            return null
        }

        return directories.get(key)
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

    override fun collectVertices(): MutableList<out RegistryVertex> = this.pocketEntrancePointers.values.toMutableList()

    public override fun type(): Type<PocketRegistry> {
        return SubsystemTypes.POCKET.value()
    }

    fun <T : Pocket<*, *>> getPocket(info: PocketInfo, clazz: Class<T>): T? =
        getPocketDirectory(info.world).getPocket<T>(info.id, clazz)

    fun getPocketEntrances(pocket: Pocket<*, *>): MutableSet<Location> {
        Objects.requireNonNull(pocket, "pocket")
        return this.getPocketEntrances(PocketInfo(pocket.world, pocket.id))
    }

    fun getPocketEntrances(info: PocketInfo?): MutableSet<Location> {
        Objects.requireNonNull<PocketInfo?>(info, "info")

        val pointer = this.pocketEntrancePointers[info] ?: return mutableSetOf<Location>()

        return RiftGraph.getInstance().targets(pointer).mapNotNull { RiftRegistry.instance.findRift(it) }.map(Rift::location).toMutableSet()
    }

    fun getPocketEntrance(pocket: Pocket<*, *>?): Location? {
        requireNotNull(pocket) { "pocket" }
        return this.getPocketEntrance(PocketInfo(pocket.world, pocket.id))
    }

    fun getPocketEntrance(info: PocketInfo?): Location? = this.getPocketEntrances(info).firstOrNull()

    fun addPocketEntrance(pocket: Pocket<*, *>?, location: Location?) {
        requireNotNull(pocket) { "pocket" }
        requireNotNull(location) { "location" }

        val info = PocketInfo(pocket.world, pocket.id)
        val pointer = this.pocketEntrancePointers.computeIfAbsent(info) { key ->
            val created = PocketEntrancePointer(key.world, key.id)
            RiftGraph.getInstance().addVertex(created)
            created
        }

        val rift = RiftRegistry.instance.getRift(location)
        if (RiftGraph.getInstance().addEdge(pointer, rift)) {
            this.setDirty()
        }
    }

    fun removePocketReferences(pocket: Pocket<*, *>?): Boolean {
        Objects.requireNonNull(pocket, "pocket")
        return this.removePocketReferences(pocket!!.world, pocket.id)
    }

    fun removePocketReferences(world: ResourceKey<Level>?, pocketId: Int): Boolean {
        requireNotNull(world) { "world" }

        val pointer = this.pocketEntrancePointers.remove(PocketInfo(world, pocketId)) ?: return false

        val affectedRifts = linkedSetOf<Rift>()
        if (RiftGraph.getInstance().containsVertex(pointer)) {
            RiftGraph.getInstance().sources(pointer).mapNotNullTo(affectedRifts) { RiftRegistry.instance.findRift(it) }
            RiftGraph.getInstance().targets(pointer).mapNotNullTo(affectedRifts) { RiftRegistry.instance.findRift(it) }

            RiftGraph.getInstance().removeVertex(pointer)
        }

        affectedRifts.forEach(Rift::markDirty)
        this.setDirty()
        return true
    }

    fun getPocketAt(location: Location?): Pocket<*, *>? = location?.worldId?.let { directories[it] }?.getPocketAt(location.blockPos)

    fun <P : Pocket<*, *>> getPocketAt(location: Location?, pocketClass: Class<P>): P? = getPocketAt(location)?.cast(pocketClass)

    companion object {
        @JvmField
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Level.RESOURCE_KEY_CODEC.unboundedMap(PocketDirectory.CODEC).fieldOf("directories").forGetter(PocketRegistry::directories),
                    unboundedMap(PocketInfo.STRING_CODEC, PocketEntrancePointer.CODEC).fieldOf("entrance_pointers").forGetter(
                        PocketRegistry::pocketEntrancePointers)
                ).apply(instance, ::PocketRegistry)
            }

        @JvmStatic
        val instance: PocketRegistry get() = getInstance(SubsystemTypes.POCKET.value())!!
    }
}
