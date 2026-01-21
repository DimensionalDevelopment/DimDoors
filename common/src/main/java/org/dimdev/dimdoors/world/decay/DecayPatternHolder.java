package org.dimdev.dimdoors.world.decay;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record DecayPatternHolder(ResourceLocation id, DecayPattern value) {
//    public static final StreamCodec<RegistryFriendlyByteBuf, DecayPatternHolder> STREAM_CODEC;

    public boolean equals(Object object) {
        return this == object || object instanceof DecayPatternHolder decayPatternHolder && this.id.equals(decayPatternHolder.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id.toString();
    }

    static {
//        STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, RecipeHolder::id, Recipe.STREAM_CODEC, RecipeHolder::value, RecipeHolder::new);
    }
}