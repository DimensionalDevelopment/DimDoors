package org.dimdev.dimdoors.util.schematic.v2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

public class SchematicBiomePalette {
	public static final UnboundedMapCodec<Biome, Integer> CODEC = Codec.unboundedMap(BuiltinRegistries.BIOME, Codec.INT);
}
