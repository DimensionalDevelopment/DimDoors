package org.dimdev.dimcore.api.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import java.io.BufferedReader
import kotlin.jvm.optionals.getOrNull

object JsonUtil {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun <T: Any> read(reader: BufferedReader, codec: Codec<T>): T? = reader.use { gson.fromJson(reader, JsonObject::class.java) }.let { JsonOps.INSTANCE.withParser(codec).apply(it) }.result().getOrNull()
}