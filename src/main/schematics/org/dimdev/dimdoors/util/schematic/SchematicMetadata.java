package org.dimdev.dimdoors.util.schematic;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

@SuppressWarnings("CodeBlock2Expr")
public final class SchematicMetadata {
	public static final SchematicMetadata EMPTY = new SchematicMetadata("", "", 0L, ImmutableList.of());
	public static final Codec<SchematicMetadata> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.STRING.fieldOf("Name").forGetter(SchematicMetadata::getName),
				Codec.STRING.fieldOf("Author").forGetter(SchematicMetadata::getAuthor),
				Codec.LONG.fieldOf("Date").forGetter(SchematicMetadata::getDate),
				Codec.list(Codec.STRING).fieldOf("RequiredMods").forGetter(SchematicMetadata::getRequiredMods)
		).apply(instance, SchematicMetadata::new);
	});
	private final String name;
	private final String author;
	private final long date;
	private final List<String> requiredMods;

	public SchematicMetadata(String name, String author, long date, List<String> requiredMods) {
		this.name = name;
		this.author = author;
		this.date = date;
		this.requiredMods = requiredMods;
	}

	public String getName() {
		return this.name;
	}

	public String getAuthor() {
		return this.author;
	}

	public long getDate() {
		return this.date;
	}

	public List<String> getRequiredMods() {
		return this.requiredMods;
	}
}
