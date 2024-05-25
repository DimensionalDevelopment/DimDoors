package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public abstract class AbstractLazyModifier implements LazyModifier {
	public static <T extends AbstractLazyModifier> Products.P1<RecordCodecBuilder.Mu<T>, String> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(Codec.STRING.optionalFieldOf("resourceKey", null).forGetter(a -> a.resourceKey));
	}

	public String resourceKey = null;

	public AbstractLazyModifier(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	@Override
	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	@Override
	public String getResourceKey() {
		return resourceKey;
	}

	@Override
	public Tag toNbt(CompoundTag nbt, boolean allowReference) {
		if (allowReference && this.getResourceKey() != null) {
			return StringTag.valueOf(this.getResourceKey());
		}
		return toNbtInternal(nbt, allowReference);
	}

	// utility so the first part of toNbt can be extracted into default method
	// at this point we know for a fact, that we need to serialize into the CompoundTag
	// overwrite in subclass
	protected CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		return this.getType().toNbt(nbt);
	}
}
