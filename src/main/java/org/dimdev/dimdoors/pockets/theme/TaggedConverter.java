package org.dimdev.dimdoors.pockets.theme;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Map;

import static org.dimdev.dimdoors.pockets.theme.Converter.ConverterType.TAGGED_CONVERTER_TYPE;

public final class TaggedConverter implements Converter {
	public static final String KEY = "tagged";

	private TagKey<Block> tag;
	private RegistryKey<Block> key;

	public TaggedConverter() {};

	public TaggedConverter(TagKey<Block> tag, RegistryKey<Block> key) {
		this.tag = tag;
		this.key = key;
	}

	public static Converter of(TagKey<Block> tagKey, Block key) {
		return new TaggedConverter(tagKey, Registry.BLOCK.getKey(key).get());
	}

	@Override
	public BlockState apply(BlockState blockState) {
		BlockState newBlockState = Registry.BLOCK.get(key).getDefaultState();

		for (Map.Entry<Property<?>, Comparable<?>> property : blockState.getEntries().entrySet()) {
			if (newBlockState.contains(property.getKey())) {
				newBlockState = newBlockState.with((Property) property.getKey(), (Comparable) property.getValue());
			}
		}

		return newBlockState;
	}

	@Override
	public boolean test(BlockState blockState) {
		return blockState.isIn(tag);
	}

	@Override
	public Converter fromNbt(NbtCompound nbt) {
		tag = TagKey.of(Registry.BLOCK_KEY, Identifier.tryParse(nbt.getString("tag")));
		key = RegistryKey.of(Registry.BLOCK_KEY, Identifier.tryParse(nbt.getString("key")));
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putString("tag", tag.id().toString());
		nbt.putString("key", key.getValue().toString());
		return Converter.super.toNbt(nbt);
	}

	@Override
	public ConverterType<? extends Converter> getType() {
		return TAGGED_CONVERTER_TYPE;
	}

}
