package org.dimdev.dimdoors.api.util

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import org.apache.logging.log4j.LogManager
import java.io.InputStream
import java.io.InputStreamReader

object ResourceUtil {
    private val LOGGER = LogManager.getLogger()

    @JvmStatic
    fun <T : Any> loadResources(manager: ResourceManager, directory: String, extension: String, read: (ResourceLocation, InputStream) -> T): Map<ResourceLocation, T> {
        val converter = FileToIdConverter(directory, extension)
        val loaded = LinkedHashMap<ResourceLocation, T>()

        for ((file, resource) in converter.listMatchingResources(manager)) {
            val id = converter.fileToId(file)
            try {
                resource.open().use { loaded[id] = read(id, it) }
            } catch (e: Exception) {
                LOGGER.error("Error loading resource {}", file, e)
            }
        }

        return loaded
    }

    @JvmStatic
    fun readJson(stream: InputStream): JsonElement = JsonParser.parseReader(InputStreamReader(stream, Charsets.UTF_8))

    @JvmStatic
    fun readCompressedNbt(stream: InputStream): CompoundTag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap())
}
