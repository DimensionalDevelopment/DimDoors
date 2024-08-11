package org.dimdev.dimdoors.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record IdCounter(int count) {
	public static final Codec<IdCounter> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("count").forGetter(IdCounter::count)).apply(instance, IdCounter::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, IdCounter> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, IdCounter::count, IdCounter::new);

	public IdCounter increment() {
		return new IdCounter(count() + 1);
	}

	public IdCounter reset() {
		return new IdCounter(0);
	}
}
