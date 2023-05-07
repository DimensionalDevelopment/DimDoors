package org.dimdev.dimdoors.util.schematic;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

@SuppressWarnings("CodeBlock2Expr")
public record SchematicMetadata(String name, String author, long date, List<String> requiredMods) {
	public static final SchematicMetadata EMPTY = new SchematicMetadata("", "", 0L, ImmutableList.of());
	public static final Codec<SchematicMetadata> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.STRING.fieldOf("Name").forGetter(SchematicMetadata::name),
				Codec.STRING.fieldOf("Author").forGetter(SchematicMetadata::author),
				Codec.LONG.fieldOf("Date").forGetter(SchematicMetadata::date),
				Codec.list(Codec.STRING).fieldOf("RequiredMods").forGetter(SchematicMetadata::requiredMods)
		).apply(instance, SchematicMetadata::new);
	});
}
