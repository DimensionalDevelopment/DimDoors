package org.dimdev.dimdoors.world.decay;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern;

import java.util.function.Consumer;

public record DecayPatternHolder(Identifier id, DecayPattern value) {
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

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public static class Builder {
        private Identifier id;
        private DecayPattern.Builder<?> pattern;

        private Builder(Identifier id) {
            this.id = id;
        }

        public Builder pattern(DecayPattern.Builder<?> pattern) {
            this.pattern = pattern;
            return this;
        }

        public DecayPatternHolder build(HolderLookup.Provider provider) {
            if (pattern == null) {
                throw new IllegalStateException("DecayPattern must not be null");
            }

            return new DecayPatternHolder(id, pattern.build(provider));
        }

        public void accept(Consumer<DecayPatternHolder> consumer, HolderLookup.Provider provider) {
            consumer.accept(build(provider));
        }
    }

    static {
//        STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, RecipeHolder::id, Recipe.STREAM_CODEC, RecipeHolder::value, RecipeHolder::new);
    }
}