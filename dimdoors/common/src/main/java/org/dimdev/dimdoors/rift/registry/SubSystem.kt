package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.MapCodec
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import org.dimdev.dimcore.api.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.api.util.NbtUtil
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.function.Supplier

/*
 * A subsystem in Dimensional Doors is a SavedData that is backed by a codec for its serialization and deserialization. They can be accessed via the getInstance method and their respective Type instance.
 */
abstract class SubSystem<T : SubSystem<T>> : SavedData() {
    abstract fun type(): Type<T>

    override fun save(compoundTag: CompoundTag, provider: HolderLookup.Provider): CompoundTag {
        return NbtUtil.serialize<T>(compoundTag, cast(), type().codec)
    }

    override fun save(file: File, registries: HolderLookup.Provider) {
        if (this.isDirty) {
            try {
                val compoundtag = CompoundTag()
                compoundtag.put("data", this.save(CompoundTag(), registries))
                NbtUtils.addCurrentDataVersion(compoundtag)

                val path = file.toPath()

                Files.createDirectories(path.parent)

                NbtIo.writeCompressed(compoundtag, file.toPath())
            } catch (ioexception: IOException) {
                DimensionalDoors.LOGGER.error("Error when saving subsytem {}", type().name.toString(), ioexception)
            }

            this.isDirty = false
        }
    }

    data class Type<T : SubSystem<T>>(
        val name: String,
        val constructor: () -> T,
        val codec: MapCodec<T>
    ) {
        fun toFilename(): String = name
    }

    companion object {
        fun <T : SubSystem<T>> getInstance(type: Type<T>): T? = getInstance(server, type)

        fun <T : SubSystem<T>> getInstance(server: MinecraftServer, type: Type<T>): T? {
            return server.overworld().dataStorage.computeIfAbsent<T>(
                Factory<T>(
                    type.constructor, { tag, _ -> NbtUtil.deserialize<T>(tag, type.codec) }, DataFixTypes.LEVEL
                ),
                type.toFilename()
            )
        }

        fun initialize(server: MinecraftServer): List<SubSystem<out SubSystem<*>>> {
            val subSystems = ModRegistries.SUBSYTEM_TYPE.mapNotNull { getInstance(server, it) }


            subSystems.filterIsInstance<RiftGraph>().forEach { graph -> graph.refreshVertices(subSystems) }

            return subSystems
        }
    }
}
