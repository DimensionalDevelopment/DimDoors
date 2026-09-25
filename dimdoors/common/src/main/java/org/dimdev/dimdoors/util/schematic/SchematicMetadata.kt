package org.dimdev.dimdoors.util.schematic

import com.google.common.collect.ImmutableList
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class SchematicMetadata(val name: String, val author: String, val date: Long, val requiredMods: List<String>) {
    companion object {
        @JvmField
        val EMPTY = SchematicMetadata("", "", 0L, ImmutableList.of())

        @JvmField
        val CODEC: Codec<SchematicMetadata> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("Name", "").forGetter(SchematicMetadata::name),
                Codec.STRING.optionalFieldOf("Author", "").forGetter(SchematicMetadata::author),
                Codec.LONG.optionalFieldOf("Date", 0L).forGetter(SchematicMetadata::date),
                Codec.list(Codec.STRING).optionalFieldOf("RequiredMods", listOf()).forGetter(SchematicMetadata::requiredMods)
            ).apply(instance, ::SchematicMetadata)
        }
    }
}
