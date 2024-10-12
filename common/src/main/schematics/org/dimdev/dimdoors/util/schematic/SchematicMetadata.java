package org.dimdev.dimdoors.util.schematic;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

@SuppressWarnings("CodeBlock2Expr")
public record SchematicMetadata(String name, String author, long date, List<String> requiredMods) {
	public static final SchematicMetadata EMPTY = new SchematicMetadata("", "", 0L, ImmutableList.of());
	public static final Codec<SchematicMetadata> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.STRING.optionalFieldOf("Name", "").forGetter(SchematicMetadata::name),
				Codec.STRING.optionalFieldOf("Author", "").forGetter(SchematicMetadata::author),
				Codec.LONG.optionalFieldOf("Date", 0L).forGetter(SchematicMetadata::date),
				Codec.list(Codec.STRING).optionalFieldOf("RequiredMods", ImmutableList.of()).forGetter(SchematicMetadata::requiredMods)
		).apply(instance, SchematicMetadata::new);
	});
}
