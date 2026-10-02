package org.dimdev.dimcore.api.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.reader
import kotlin.jvm.optionals.getOrNull

data class ConfigType<T: Any>(val codec: Codec<T>, val supplier: () -> T) {
    fun load(path: Path) : T {
        if (path.notExists()) {
            val config = supplier.invoke()
            save(path,config)
            return config
        }

        path.reader().use {
            val obj = gson.fromJson(it, JsonElement::class.java)

            return JsonOps.INSTANCE.withParser(codec).apply(obj).result().orElseThrow()
        }
    }

    fun save(path: Path, config: T) {
        path.parent.createDirectories()

        path.bufferedWriter().use { writer ->
            val obj = JsonOps.INSTANCE.withEncoder(codec).apply(config).result().getOrNull() ?: return

            gson.toJson(obj, writer)
        }
    }

    companion object {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }
}
